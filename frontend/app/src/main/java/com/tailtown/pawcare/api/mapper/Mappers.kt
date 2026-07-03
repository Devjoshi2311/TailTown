package com.tailtown.pawcare.api.mapper

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.api.model.BookingDto
import com.tailtown.pawcare.api.model.BookingStatus
import com.tailtown.pawcare.api.model.PetDto
import com.tailtown.pawcare.api.model.PetSpecies
import com.tailtown.pawcare.api.model.VetDto
import com.tailtown.pawcare.api.model.VisitType
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.vet.UrgencyStyle
import com.tailtown.pawcare.ui.vet.UpcomingBooking
import com.tailtown.pawcare.ui.vet.Vet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ── VetDto → Vet (UI model used by VetDirectoryScreen + VetDetailScreen) ──

fun VetDto.toUiModel(): Vet = Vet(
    id = id,
    name = name,
    specialty = specialty,
    location = location.area,
    fullLocation = "${location.area}, ${location.city}",
    rating = reviews.rating,
    reviewCount = reviews.reviewCount,
    yearsExperience = yearsExperience,
    languages = languages,
    pricePerVisit = pricePerVisit,
    homeVisitAvailable = homeVisitAvailable,
    isSuperhost = isSuperhost,
    certifications = "",
    heroTint = specialty.toHeroTint(),
)

private fun String.toHeroTint(): Color = when (lowercase().trim()) {
    "surgery", "orthopaedics"    -> Color(0xFFE8F4FF)   // cool blue
    "dermatology"                -> Color(0xFFFFF3E0)   // warm amber
    "dental", "dentistry"        -> Color(0xFFE8F5E9)   // soft green
    "ophthalmology"              -> Color(0xFFF3E5F5)   // lavender
    else                         -> CoralSoft            // default coral
}

// ── BookingDto → UpcomingBooking (MyBookingsScreen card) ──────────────────

fun BookingDto.toUpcomingBooking(): UpcomingBooking {
    val (urgencyLabel, urgencyStyle) = computeUrgency(date, status)
    return UpcomingBooking(
        urgencyLabel = urgencyLabel,
        urgencyStyle = urgencyStyle,
        doctorName = vet.name,
        purpose = purpose,
        dateLabel = formatDateLabel(date),
        timeLabel = time,
        isVideo = visitType == VisitType.VIDEO,
    )
}

private fun computeUrgency(
    isoDate: String,
    status: BookingStatus,
): Pair<String, UrgencyStyle> {
    if (status == BookingStatus.CANCELLED) return "Cancelled" to UrgencyStyle.AMBER
    if (status == BookingStatus.COMPLETED) return "Completed" to UrgencyStyle.AMBER

    return try {
        val bookingDate = LocalDate.parse(isoDate.substring(0, 10))
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(today, bookingDate)
        when {
            days < 0  -> "Missed"          to UrgencyStyle.AMBER
            days == 0L -> "Today"          to UrgencyStyle.CORAL
            days == 1L -> "Tomorrow"       to UrgencyStyle.CORAL
            days <= 3  -> "In $days days"  to UrgencyStyle.CORAL
            days <= 7  -> "This week"      to UrgencyStyle.AMBER
            days <= 14 -> "Next week"      to UrgencyStyle.AMBER
            else       -> "Upcoming"       to UrgencyStyle.AMBER
        }
    } catch (_: Exception) {
        "Upcoming" to UrgencyStyle.AMBER
    }
}

private val displayFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")

private fun formatDateLabel(isoDate: String): String = try {
    LocalDate.parse(isoDate.substring(0, 10)).format(displayFormatter)
} catch (_: Exception) {
    isoDate
}

// ── PetDto helpers (BookedScreen summary row) ─────────────────────────────

fun PetDto.speciesEmoji(): String = when (species) {
    PetSpecies.DOG   -> "🐶"
    PetSpecies.CAT   -> "🐱"
    PetSpecies.BIRD  -> "🐦"
    PetSpecies.OTHER -> "🐾"
}

fun PetDto.displayAge(): String = ageYears?.let { "${it} yr" } ?: "—"
