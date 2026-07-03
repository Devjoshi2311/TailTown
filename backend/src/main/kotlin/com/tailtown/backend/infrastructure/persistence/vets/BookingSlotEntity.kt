package com.tailtown.backend.infrastructure.persistence.vets

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "booking_slots")
class BookingSlotEntity(

    @Column(name = "vet_id", nullable = false, columnDefinition = "uuid")
    var vetId: UUID,

    @Column(name = "service_type", nullable = false)
    var serviceType: String,

    @Column(name = "starts_at", nullable = false)
    var startsAt: Instant,

    @Column(name = "ends_at", nullable = false)
    var endsAt: Instant,

    @Column(name = "status", nullable = false)
    var status: String = "AVAILABLE",

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "hold_expires_at")
    var holdExpiresAt: Instant? = null,

    @Column(name = "held_by_user_id", columnDefinition = "uuid")
    var heldByUserId: UUID? = null

) : AuditableEntity()
