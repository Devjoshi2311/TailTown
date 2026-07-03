package com.tailtown.backend.api.v1.booking

import com.tailtown.backend.infrastructure.persistence.booking.BookingEntity
import java.time.Instant
import java.util.UUID

data class BookingResponse(
    val id: UUID,
    val userId: UUID,
    val petId: UUID,
    val vetId: UUID,
    val slotId: UUID,
    val serviceType: String,
    val visitType: String,
    val scheduledStart: Instant,
    val scheduledEnd: Instant,
    val status: String,
    val addressSnapshot: String?,
    val notes: String?,
    val version: Long
) {
    companion object {
        fun from(entity: BookingEntity): BookingResponse = BookingResponse(
            id = entity.id,
            userId = entity.userId,
            petId = entity.petId,
            vetId = entity.vetId,
            slotId = entity.slotId,
            serviceType = entity.serviceType,
            visitType = entity.visitType,
            scheduledStart = entity.scheduledStart,
            scheduledEnd = entity.scheduledEnd,
            status = entity.status,
            addressSnapshot = entity.addressSnapshot,
            notes = entity.notes,
            version = entity.version
        )
    }
}
