package com.tailtown.backend.application.payments

import com.fasterxml.jackson.databind.ObjectMapper
import com.tailtown.backend.infrastructure.persistence.orders.OrderEntity
import com.tailtown.backend.infrastructure.persistence.orders.OrderItemRepository
import com.tailtown.backend.infrastructure.persistence.orders.OrderRepository
import com.tailtown.backend.infrastructure.persistence.shop.ProductRepository
import com.tailtown.backend.platform.exception.ConflictException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class PaymentService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val razorpayGatewayClient: RazorpayGatewayClient,
    private val webhookIdempotencyGuard: WebhookIdempotencyGuard,
    private val bookingPaymentService: BookingPaymentService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    @Transactional
    fun verifyAndCapture(
        userId: UUID,
        orderId: UUID,
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String
    ): OrderEntity {
        val order = orderRepository.findByIdAndUserIdAndDeletedAtIsNull(orderId, userId)
            ?: throw ResourceNotFoundException("Order", orderId)

        if (order.paymentStatus == "PAID") {
            log.info("Order {} already PAID — treating /payments/verify call as an idempotent replay", order.id)
            return order
        }

        if (order.razorpayOrderId != razorpayOrderId) {
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Order/payment mismatch")
        }

        if (!razorpayGatewayClient.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
            log.warn("Signature verification failed for order {}", order.id)
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Payment signature verification failed")
        }

        // Double-check against Razorpay's own record of the payment — never trust the client's claim of success.
        val payment = razorpayGatewayClient.fetchPayment(razorpayPaymentId)
        val expectedAmountPaise = order.grandTotal.movePointRight(2).longValueExact()
        val verified = payment.status == "captured" &&
            payment.orderId == razorpayOrderId &&
            payment.amountPaise == expectedAmountPaise &&
            payment.currency == order.currency

        if (!verified) {
            log.warn(
                "Payment {} failed verification for order {}: status={} orderId={} amountPaise={} currency={}",
                razorpayPaymentId, order.id, payment.status, payment.orderId, payment.amountPaise, payment.currency
            )
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Payment could not be verified")
        }

        return markPaid(order, razorpayPaymentId)
    }

    @Transactional
    fun handleWebhook(rawBody: String, signatureHeader: String) {
        if (!razorpayGatewayClient.verifyWebhookSignature(rawBody, signatureHeader)) {
            log.warn("Rejected webhook with invalid signature")
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Invalid webhook signature")
        }

        val json = objectMapper.readTree(rawBody)
        val eventId = json.get("id")?.asText()
        val eventType = json.get("event")?.asText().orEmpty()
        if (eventId.isNullOrBlank()) {
            log.warn("Webhook payload missing event id, ignoring")
            return
        }

        if (!webhookIdempotencyGuard.recordIfNew(eventId, eventType, rawBody)) {
            log.info("Webhook event {} already processed, skipping", eventId)
            return
        }

        val paymentEntity = json.at("/payload/payment/entity")
        val razorpayOrderId = paymentEntity.get("order_id")?.asText()
        val paymentId = paymentEntity.get("id")?.asText().orEmpty()
        if (razorpayOrderId.isNullOrBlank()) {
            log.info("Webhook event {} ({}) has no payment order_id, ignoring", eventId, eventType)
            return
        }

        // Razorpay's account-wide webhook doesn't distinguish shop orders from vet-booking
        // payments — try an order first, then fall back to a booking with the same gateway order id.
        val order = orderRepository.findByRazorpayOrderIdAndDeletedAtIsNull(razorpayOrderId)
        if (order != null) {
            when (eventType) {
                "payment.captured" -> markPaid(order, paymentId)
                "payment.failed" -> markFailed(order)
                else -> log.info("Ignoring webhook event type {}", eventType)
            }
            return
        }

        val booking = bookingPaymentService.findByRazorpayOrderId(razorpayOrderId)
        if (booking != null) {
            when (eventType) {
                "payment.captured" -> bookingPaymentService.markPaid(booking, paymentId)
                "payment.failed" -> bookingPaymentService.markFailed(booking)
                else -> log.info("Ignoring webhook event type {}", eventType)
            }
            return
        }

        log.warn("Webhook event {} referenced unknown razorpay order {}", eventId, razorpayOrderId)
    }

    @Transactional
    fun reconcilePending(staleAfter: Duration = Duration.ofMinutes(15), failAfter: Duration = Duration.ofHours(1)) {
        val cutoff = Instant.now().minus(staleAfter)
        val stuck = orderRepository
            .findAllByPaymentStatusAndRazorpayOrderIdIsNotNullAndCreatedAtBeforeAndDeletedAtIsNull("PENDING", cutoff)

        stuck.forEach { order ->
            try {
                val payments = razorpayGatewayClient.fetchOrderPayments(order.razorpayOrderId!!)
                val captured = payments.firstOrNull { it.status == "captured" }
                when {
                    captured != null -> markPaid(order, captured.id)
                    Duration.between(order.createdAt, Instant.now()) > failAfter -> markFailed(order)
                }
            } catch (e: Exception) {
                log.error("Reconciliation failed for order {}", order.id, e)
            }
        }

        bookingPaymentService.reconcilePending(staleAfter, failAfter)
    }

    private fun markPaid(order: OrderEntity, paymentId: String): OrderEntity {
        if (order.paymentStatus == "PAID") return order
        order.paymentStatus = "PAID"
        order.status = "PLACED"
        order.razorpayPaymentId = paymentId
        order.placedAt = order.placedAt ?: Instant.now()
        log.info("Order {} marked PAID via payment {}", order.id, paymentId)
        return orderRepository.save(order)
    }

    private fun markFailed(order: OrderEntity) {
        // Only fires from PENDING — can never downgrade an already-PAID order, and never double-releases stock.
        if (order.paymentStatus != "PENDING") return
        order.paymentStatus = "FAILED"
        order.status = "PAYMENT_FAILED"
        orderRepository.save(order)
        releaseStock(order.id)
        log.info("Order {} marked FAILED, stock released", order.id)
    }

    private fun releaseStock(orderId: UUID) {
        orderItemRepository.findAllByOrderIdAndDeletedAtIsNull(orderId).forEach { item ->
            val productId = item.productId ?: return@forEach
            productRepository.findById(productId).ifPresent { product ->
                product.stockQty += item.quantity
                productRepository.save(product)
            }
        }
    }
}
