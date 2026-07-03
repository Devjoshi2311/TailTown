package com.tailtown.backend.infrastructure.persistence.booking

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bookings")
class BookingEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "pet_id", nullable = false, columnDefinition = "uuid")
    var petId: UUID,

    @Column(name = "vet_id", nullable = false, columnDefinition = "uuid")
    var vetId: UUID,

    @Column(name = "slot_id", nullable = false, columnDefinition = "uuid")
    var slotId: UUID,

    @Column(name = "service_type", nullable = false)
    var serviceType: String,

    @Column(name = "visit_type", nullable = false)
    var visitType: String = "CLINIC",

    @Column(name = "scheduled_start", nullable = false)
    var scheduledStart: Instant,

    @Column(name = "scheduled_end", nullable = false)
    var scheduledEnd: Instant,

    @Column(name = "status", nullable = false)
    var status: String = "CONFIRMED",

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "razorpay_order_id")
    var razorpayOrderId: String? = null,

    @Column(name = "razorpay_payment_id")
    var razorpayPaymentId: String? = null,

    @Column(name = "address_id", columnDefinition = "uuid")
    var addressId: UUID? = null,

    @Column(name = "address_snapshot", columnDefinition = "TEXT")
    var addressSnapshot: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

    @Column(name = "cancelled_at")
    var cancelledAt: Instant? = null,

    @Column(name = "cancelled_by", columnDefinition = "uuid")
    var cancelledBy: UUID? = null,

    @Column(name = "cancellation_reason")
    var cancellationReason: String? = null

) : AuditableEntity()
