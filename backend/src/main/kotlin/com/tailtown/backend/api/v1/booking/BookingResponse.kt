package com.tailtown.backend.api.v1.booking

import com.tailtown.backend.infrastructure.persistence.booking.BookingEntity
import java.math.BigDecimal
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
    val amount: BigDecimal,
    val currency: String,
    val addressSnapshot: String?,
    val notes: String?,
    val version: Long,
    // Only populated while status == "PENDING_PAYMENT" — everything the client needs to open Razorpay Checkout.
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val amountInPaise: Long? = null
) {
    companion object {
        fun from(entity: BookingEntity, razorpayKeyId: String? = null): BookingResponse = BookingResponse(
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
            amount = entity.amount,
            currency = entity.currency,
            addressSnapshot = entity.addressSnapshot,
            notes = entity.notes,
            version = entity.version,
            razorpayOrderId = entity.razorpayOrderId.takeIf { entity.status == "PENDING_PAYMENT" },
            razorpayKeyId = razorpayKeyId.takeIf { entity.status == "PENDING_PAYMENT" },
            amountInPaise = entity.amount.movePointRight(2).longValueExact().takeIf { entity.status == "PENDING_PAYMENT" }
        )
    }
}
