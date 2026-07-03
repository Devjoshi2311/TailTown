package com.tailtown.backend.application.orders

import com.tailtown.backend.application.payments.RazorpayGatewayClient
import com.tailtown.backend.infrastructure.persistence.cart.CartItemRepository
import com.tailtown.backend.infrastructure.persistence.cart.CartRepository
import com.tailtown.backend.infrastructure.persistence.orders.OrderEntity
import com.tailtown.backend.infrastructure.persistence.orders.OrderItemEntity
import com.tailtown.backend.infrastructure.persistence.orders.OrderItemRepository
import com.tailtown.backend.infrastructure.persistence.orders.OrderRepository
import com.tailtown.backend.infrastructure.persistence.profile.AddressRepository
import com.tailtown.backend.infrastructure.persistence.shop.ProductRepository
import com.tailtown.backend.platform.exception.ConflictException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ForbiddenException
import com.tailtown.backend.platform.exception.OutOfStockException
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository,
    private val razorpayGatewayClient: RazorpayGatewayClient
) {

    companion object {
        private val RANDOM = SecureRandom()
        private val ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        private fun generateOrderNumber(): String {
            val suffix = (1..8)
                .map { ALPHANUMERIC[RANDOM.nextInt(ALPHANUMERIC.length)] }
                .joinToString("")
            return "ORD-$suffix"
        }
    }

    fun createOrder(userId: UUID, addressId: UUID, idempotencyKey: String?): OrderEntity {
        val cart = cartRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, "ACTIVE")
            ?: throw ConflictException(ErrorCode.CART_EMPTY, "No active cart found")

        val cartItems = cartItemRepository.findAllByCartIdAndDeletedAtIsNull(cart.id)
        if (cartItems.isEmpty()) {
            throw ConflictException(ErrorCode.CART_EMPTY, "Cart is empty")
        }

        val address = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
            ?: throw ForbiddenException("Address not found or does not belong to the user")

        val productIds = cartItems.map { it.productId }.toSet()
        val productMap = productRepository.findAllById(productIds).associateBy { it.id }

        // Re-validate stock and decrement
        for (item in cartItems) {
            val product = productMap[item.productId]
                ?: throw ResourceNotFoundException("Product", item.productId)
            if (product.stockQty < item.quantity) {
                throw OutOfStockException(item.productId)
            }
        }

        // Decrement stock
        for (item in cartItems) {
            val product = productMap[item.productId]!!
            product.stockQty = product.stockQty - item.quantity
            productRepository.save(product)
        }

        // Compute totals
        val subtotal = cartItems.fold(BigDecimal.ZERO) { acc, item ->
            val price = item.priceSnapshot ?: productMap[item.productId]?.price ?: BigDecimal.ZERO
            acc.add(price.multiply(BigDecimal(item.quantity)))
        }
        val deliveryFee = BigDecimal.ZERO
        val discountTotal = BigDecimal.ZERO
        val taxTotal = BigDecimal.ZERO
        val grandTotal = subtotal.add(deliveryFee).subtract(discountTotal).add(taxTotal)

        // Build address snapshot
        val addressSnapshot = buildAddressSnapshot(address)

        // Create order — payment is confirmed only after Razorpay verification/webhook (see PaymentService)
        var order = orderRepository.save(
            OrderEntity(
                orderNumber = generateOrderNumber(),
                userId = userId,
                cartId = cart.id,
                addressId = addressId,
                deliveryAddressSnapshot = addressSnapshot,
                status = "PENDING_PAYMENT",
                subtotal = subtotal,
                discountTotal = discountTotal,
                deliveryFee = deliveryFee,
                taxTotal = taxTotal,
                grandTotal = grandTotal,
                currency = cart.currency,
                paymentStatus = "PENDING"
            )
        )

        // Create the matching Razorpay order using the server-computed grand total — never a client-supplied amount.
        // If this throws, the whole transaction rolls back (order row, stock decrement, cart conversion).
        val amountPaise = grandTotal.movePointRight(2).longValueExact()
        order.razorpayOrderId = razorpayGatewayClient.createOrder(amountPaise, cart.currency, order.orderNumber)
        order = orderRepository.save(order)

        // Create order items
        cartItems.forEach { item ->
            val product = productMap[item.productId]!!
            val unitPrice = item.priceSnapshot ?: product.price
            val lineTotal = unitPrice.multiply(BigDecimal(item.quantity))
            orderItemRepository.save(
                OrderItemEntity(
                    orderId = order.id,
                    productId = item.productId,
                    sku = product.sku,
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    quantity = item.quantity,
                    unitPrice = unitPrice,
                    lineDiscount = BigDecimal.ZERO,
                    lineTax = BigDecimal.ZERO,
                    lineTotal = lineTotal,
                    currency = product.currency
                )
            )
        }

        // Convert cart
        cart.status = "CONVERTED"
        cartRepository.save(cart)

        return order
    }

    @Transactional(readOnly = true)
    fun listOrders(userId: UUID, pageable: Pageable): Page<OrderEntity> =
        orderRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)

    @Transactional(readOnly = true)
    fun getOrder(userId: UUID, orderId: UUID): OrderEntity =
        orderRepository.findByIdAndUserIdAndDeletedAtIsNull(orderId, userId)
            ?: throw ResourceNotFoundException("Order", orderId)

    @Transactional(readOnly = true)
    fun getOrderItems(orderId: UUID): List<OrderItemEntity> =
        orderItemRepository.findAllByOrderIdAndDeletedAtIsNull(orderId)

    private fun buildAddressSnapshot(address: com.tailtown.backend.infrastructure.persistence.profile.AddressEntity): String {
        val parts = listOfNotNull(
            address.recipientName,
            address.line1,
            address.line2,
            address.landmark,
            address.city,
            address.state,
            address.pincode,
            address.country
        )
        return parts.joinToString(", ")
    }
}
