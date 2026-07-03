package com.tailtown.backend.application.payments

import com.tailtown.backend.infrastructure.persistence.booking.BookingEntity
import com.tailtown.backend.infrastructure.persistence.booking.BookingRepository
import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotRepository
import com.tailtown.backend.platform.exception.ConflictException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * Mirrors PaymentService's order flow for vet-booking reservations: a slot is HELD (not booked
 * outright) while payment is in flight, and only flips to BOOKED once Razorpay confirms capture.
 */
@Service
class BookingPaymentService(
    private val bookingRepository: BookingRepository,
    private val bookingSlotRepository: BookingSlotRepository,
    private val razorpayGatewayClient: RazorpayGatewayClient
) {
    private val log = LoggerFactory.getLogger(BookingPaymentService::class.java)

    @Transactional
    fun verifyAndCapture(
        userId: UUID,
        bookingId: UUID,
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String
    ): BookingEntity {
        val booking = bookingRepository.findByIdAndUserIdAndDeletedAtIsNull(bookingId, userId)
            ?: throw ResourceNotFoundException("Booking", bookingId)

        if (booking.status == "CONFIRMED") {
            log.info("Booking {} already CONFIRMED — treating /payments/verify-booking call as an idempotent replay", booking.id)
            return booking
        }

        if (booking.razorpayOrderId != razorpayOrderId) {
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Booking/payment mismatch")
        }

        if (!razorpayGatewayClient.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
            log.warn("Signature verification failed for booking {}", booking.id)
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Payment signature verification failed")
        }

        val payment = razorpayGatewayClient.fetchPayment(razorpayPaymentId)
        val expectedAmountPaise = booking.amount.movePointRight(2).longValueExact()
        val verified = payment.status == "captured" &&
            payment.orderId == razorpayOrderId &&
            payment.amountPaise == expectedAmountPaise &&
            payment.currency == booking.currency

        if (!verified) {
            log.warn(
                "Payment {} failed verification for booking {}: status={} orderId={} amountPaise={} currency={}",
                razorpayPaymentId, booking.id, payment.status, payment.orderId, payment.amountPaise, payment.currency
            )
            throw ConflictException(ErrorCode.PAYMENT_DECLINED, "Payment could not be verified")
        }

        return markPaid(booking, razorpayPaymentId)
    }

    fun findByRazorpayOrderId(razorpayOrderId: String): BookingEntity? =
        bookingRepository.findByRazorpayOrderIdAndDeletedAtIsNull(razorpayOrderId)

    @Transactional
    fun markPaid(booking: BookingEntity, paymentId: String): BookingEntity {
        if (booking.status == "CONFIRMED") return booking
        booking.status = "CONFIRMED"
        booking.razorpayPaymentId = paymentId
        val saved = bookingRepository.save(booking)

        bookingSlotRepository.findByIdForUpdate(booking.slotId)?.let { slot ->
            if (slot.status == "HELD") {
                slot.status = "BOOKED"
                slot.heldByUserId = null
                slot.holdExpiresAt = null
                bookingSlotRepository.save(slot)
            }
        }
        log.info("Booking {} marked CONFIRMED via payment {}", booking.id, paymentId)
        return saved
    }

    @Transactional
    fun markFailed(booking: BookingEntity) {
        // Only fires from PENDING_PAYMENT — can never downgrade a CONFIRMED booking, never double-releases the slot.
        if (booking.status != "PENDING_PAYMENT") return
        booking.status = "PAYMENT_FAILED"
        bookingRepository.save(booking)

        bookingSlotRepository.findByIdForUpdate(booking.slotId)?.let { slot ->
            if (slot.status == "HELD") {
                slot.status = "AVAILABLE"
                slot.heldByUserId = null
                slot.holdExpiresAt = null
                bookingSlotRepository.save(slot)
            }
        }
        log.info("Booking {} marked PAYMENT_FAILED, slot released", booking.id)
    }

    @Transactional
    fun reconcilePending(staleAfter: Duration = Duration.ofMinutes(15), failAfter: Duration = Duration.ofHours(1)) {
        val cutoff = Instant.now().minus(staleAfter)
        val stuck = bookingRepository
            .findAllByStatusAndRazorpayOrderIdIsNotNullAndCreatedAtBeforeAndDeletedAtIsNull("PENDING_PAYMENT", cutoff)

        stuck.forEach { booking ->
            try {
                val payments = razorpayGatewayClient.fetchOrderPayments(booking.razorpayOrderId!!)
                val captured = payments.firstOrNull { it.status == "captured" }
                when {
                    captured != null -> markPaid(booking, captured.id)
                    Duration.between(booking.createdAt, Instant.now()) > failAfter -> markFailed(booking)
                }
            } catch (e: Exception) {
                log.error("Reconciliation failed for booking {}", booking.id, e)
            }
        }
    }
}
