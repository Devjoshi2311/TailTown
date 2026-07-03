package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Shared enums ───────────────────────────────────────────────────────────

@Serializable
enum class BookingStatus {
    @SerialName("upcoming")   UPCOMING,
    @SerialName("completed")  COMPLETED,
    @SerialName("cancelled")  CANCELLED,
}

@Serializable
enum class VisitType {
    @SerialName("clinic")     CLINIC,
    @SerialName("home")       HOME,
    @SerialName("video")      VIDEO,
}

@Serializable
enum class PaymentMethod {
    @SerialName("upi")        UPI,
    @SerialName("card")       CARD,
    @SerialName("cash")       CASH,
    @SerialName("wallet")     WALLET,
}

// ── BookingDto — used in Booked screen + MyBookings screen ─────────────────

@Serializable
data class BookingDto(
    val id: String,
    @SerialName("vet_id") val vetId: String,
    val vet: VetDto,
    @SerialName("pet_id") val petId: String,
    val pet: PetDto,
    val date: String,                               // ISO-8601 "2024-06-14"
    val time: String,                               // "11:30"
    @SerialName("slot_id") val slotId: String,
    val status: BookingStatus,
    @SerialName("visit_type") val visitType: VisitType,
    @SerialName("clinic_name") val clinicName: String? = null,
    val purpose: String,                            // "Check-up", "Vaccination"
    @SerialName("amount_paid") val amountPaid: Int, // INR, 0 if unpaid
    @SerialName("payment_method") val paymentMethod: PaymentMethod? = null,
    @SerialName("payment_status") val paymentStatus: String,  // "paid" | "pending"
    @SerialName("directions_url") val directionsUrl: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

// ── POST /bookings  (VetDetailScreen "Reserve" button) ────────────────────

@Serializable
data class CreateBookingRequest(
    @SerialName("vet_id") val vetId: String,
    @SerialName("pet_id") val petId: String,
    val date: String,                               // ISO-8601
    @SerialName("slot_id") val slotId: String,
    val purpose: String,
    @SerialName("visit_type") val visitType: VisitType,
)

/** ApiResponse<BookingData> */
@Serializable
data class BookingData(
    val booking: BookingDto,
)

// ── GET /bookings  (MyBookingsScreen) ─────────────────────────────────────

/** Query parameters for the bookings list. */
@Serializable
data class BookingListParams(
    val status: BookingStatus? = null,  // null → all
    @SerialName("pet_id") val petId: String? = null,
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
)

/** ApiResponse<BookingListData> */
@Serializable
data class BookingListData(
    val bookings: List<BookingDto>,
)

// ── PATCH /bookings/{id}/reschedule  (MyBookingsScreen "Reschedule") ───────

@Serializable
data class RescheduleBookingRequest(
    val date: String,                   // new ISO-8601 date
    @SerialName("slot_id") val slotId: String,
)

// ── DELETE /bookings/{id}  (MyBookingsScreen "Cancel") ─────────────────────

@Serializable
data class CancelBookingRequest(
    val reason: String? = null,
)
