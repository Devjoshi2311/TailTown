package com.tailtown.backend.infrastructure.persistence.orders

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
class OrderEntity(

    @Column(name = "order_number", nullable = false, unique = true)
    var orderNumber: String,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "cart_id", columnDefinition = "uuid")
    var cartId: UUID? = null,

    @Column(name = "address_id", columnDefinition = "uuid")
    var addressId: UUID? = null,

    @Column(name = "delivery_address_snapshot", nullable = false, columnDefinition = "TEXT")
    var deliveryAddressSnapshot: String,

    @Column(name = "status", nullable = false)
    var status: String = "PENDING_PAYMENT",

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    var subtotal: BigDecimal,

    @Column(name = "discount_total", nullable = false, precision = 19, scale = 2)
    var discountTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 2)
    var deliveryFee: BigDecimal = BigDecimal.ZERO,

    @Column(name = "tax_total", nullable = false, precision = 19, scale = 2)
    var taxTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "grand_total", nullable = false, precision = 19, scale = 2)
    var grandTotal: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "payment_status", nullable = false)
    var paymentStatus: String = "PENDING",

    @Column(name = "razorpay_order_id")
    var razorpayOrderId: String? = null,

    @Column(name = "razorpay_payment_id")
    var razorpayPaymentId: String? = null,

    @Column(name = "placed_at")
    var placedAt: Instant? = null,

    @Column(name = "cancelled_at")
    var cancelledAt: Instant? = null,

    @Column(name = "cancelled_by", columnDefinition = "uuid")
    var cancelledBy: UUID? = null,

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    var cancellationReason: String? = null

) : AuditableEntity()
