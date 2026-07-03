package com.tailtown.backend.api.v1.vets

import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class SlotResponse(
    val id: UUID,
    val vetId: UUID,
    val serviceType: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val status: String,
    val price: BigDecimal
) {
    companion object {
        fun from(entity: BookingSlotEntity): SlotResponse = SlotResponse(
            id = entity.id,
            vetId = entity.vetId,
            serviceType = entity.serviceType,
            startsAt = entity.startsAt,
            endsAt = entity.endsAt,
            status = entity.status,
            price = entity.price
        )
    }
}
