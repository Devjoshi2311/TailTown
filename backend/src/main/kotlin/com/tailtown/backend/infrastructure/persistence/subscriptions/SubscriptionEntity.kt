package com.tailtown.backend.infrastructure.persistence.subscriptions

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "subscriptions")
class SubscriptionEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    var productId: UUID,

    @Column(name = "address_id", columnDefinition = "uuid")
    var addressId: UUID? = null,

    @Column(name = "status", nullable = false)
    var status: String = "ACTIVE",

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1,

    @Column(name = "cadence", nullable = false)
    var cadence: String,

    @Column(name = "price_per_cycle", nullable = false, precision = 12, scale = 2)
    var pricePerCycle: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "next_billing_date")
    var nextBillingDate: LocalDate? = null,

    @Column(name = "next_delivery_date", nullable = false)
    var nextDeliveryDate: LocalDate,

    @Column(name = "paused_until")
    var pausedUntil: LocalDate? = null,

    @Column(name = "cancelled_at")
    var cancelledAt: Instant? = null,

    @Column(name = "cancellation_reason")
    var cancellationReason: String? = null

) : AuditableEntity()
