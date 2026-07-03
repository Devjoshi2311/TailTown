package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Shared sub-objects ─────────────────────────────────────────────────────

@Serializable
data class LocationDto(
    val area: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("distance_km") val distanceKm: Float? = null,  // populated when user location is known
)

@Serializable
data class CertificationDto(
    val title: String,
    val issuer: String,
    val year: Int? = null,
)

@Serializable
data class ReviewSummaryDto(
    val rating: Float,
    @SerialName("review_count") val reviewCount: Int,
    @SerialName("five_star_pct") val fiveStarPct: Int,
)

// ── VetDto — used in both list and detail responses ────────────────────────

@Serializable
data class VetDto(
    val id: String,
    val name: String,
    val specialty: String,                          // "Small animals", "Surgery", …
    val location: LocationDto,
    val reviews: ReviewSummaryDto,
    @SerialName("years_experience") val yearsExperience: Int,
    val languages: List<String>,
    @SerialName("price_per_visit") val pricePerVisit: Int,      // INR
    @SerialName("home_visit_available") val homeVisitAvailable: Boolean,
    @SerialName("is_superhost") val isSuperhost: Boolean,
    @SerialName("available_now") val availableNow: Boolean = false,
    @SerialName("photo_urls") val photoUrls: List<String> = emptyList(),
    val certifications: List<CertificationDto> = emptyList(),
    val bio: String? = null,
)

// ── GET /vets  (VetDirectoryScreen) ───────────────────────────────────────

/** Query parameters — serialized as URL query string by the HTTP client. */
@Serializable
data class VetFilterParams(
    val filter: String? = null,         // "available_now" | "home_visit" | "surgery"
    val latitude: Double? = null,
    val longitude: Double? = null,
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
)

/** ApiResponse<VetListData> */
@Serializable
data class VetListData(
    val vets: List<VetDto>,
)

// ── GET /vets/{id}  (VetDetailScreen header) ──────────────────────────────

/** ApiResponse<VetDetailData> */
@Serializable
data class VetDetailData(
    val vet: VetDto,
)

// ── GET /vets/{id}/available-dates  (VetDetailScreen date picker) ──────────

@Serializable
data class DateSlotDto(
    val date: String,                   // ISO-8601 "2024-06-14"
    @SerialName("day_label") val dayLabel: String,   // "Mon"
    @SerialName("day_num") val dayNum: Int,           // 14
    @SerialName("month_label") val monthLabel: String, // "Jun"
    val available: Boolean,
)

/** ApiResponse<AvailableDatesData> */
@Serializable
data class AvailableDatesData(
    @SerialName("vet_id") val vetId: String,
    val dates: List<DateSlotDto>,
)

// ── GET /vets/{id}/slots?date=ISO_DATE  (VetDetailScreen time picker) ──────

@Serializable
data class TimeSlotDto(
    val time: String,                   // "10:00", "11:30", "14:00"
    val available: Boolean,
    @SerialName("slot_id") val slotId: String,       // stable ID used in BookingRequest
)

/** ApiResponse<AvailableSlotsData> */
@Serializable
data class AvailableSlotsData(
    @SerialName("vet_id") val vetId: String,
    val date: String,
    val slots: List<TimeSlotDto>,
)
