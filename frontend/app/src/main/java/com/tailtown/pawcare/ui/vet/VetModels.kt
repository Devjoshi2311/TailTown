package com.tailtown.pawcare.ui.vet

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.ui.theme.CoralSoft

data class Vet(
    val id: String,
    val name: String,
    val specialty: String,
    val location: String,
    val fullLocation: String,
    val rating: Float,
    val reviewCount: Int,
    val yearsExperience: Int,
    val languages: List<String>,
    val pricePerVisit: Int,
    val homeVisitAvailable: Boolean,
    val isSuperhost: Boolean,
    val certifications: String,
    val heroTint: Color,
    val imageUrl: String? = null,
    val images: List<String> = emptyList(),
)

data class UpcomingBooking(
    val bookingId: String = "",
    val status: String = "CONFIRMED",
    val version: Long = 0,
    val scheduledStartEpoch: Long = 0L,
    val urgencyLabel: String,
    val urgencyStyle: UrgencyStyle,
    val doctorName: String,
    val purpose: String,
    val dateLabel: String,
    val timeLabel: String,
    val isVideo: Boolean = false,
)

enum class UrgencyStyle { CORAL, AMBER }

val sampleVets = listOf(
    Vet(
        id = "1",
        name = "Dr. Anjali Mehta",
        specialty = "Small animals",
        location = "Indirapuram",
        fullLocation = "Indirapuram, GZB",
        rating = 4.9f,
        reviewCount = 214,
        yearsExperience = 8,
        languages = listOf("Hindi", "English"),
        pricePerVisit = 600,
        homeVisitAvailable = true,
        isSuperhost = true,
        certifications = "BVSc, MCI Certified",
        heroTint = CoralSoft,
    ),
    Vet(
        id = "2",
        name = "Dr. Rohan Shah",
        specialty = "Surgery",
        location = "Vasundhara",
        fullLocation = "Vasundhara, GZB",
        rating = 4.7f,
        reviewCount = 89,
        yearsExperience = 12,
        languages = listOf("Hindi"),
        pricePerVisit = 800,
        homeVisitAvailable = false,
        isSuperhost = false,
        certifications = "MS (Veterinary Surgery)",
        heroTint = Color(0xFFE8F4FF),
    ),
)

val sampleBookings = listOf(
    UpcomingBooking(
        urgencyLabel = "In 2 days",
        urgencyStyle = UrgencyStyle.CORAL,
        doctorName = "Dr. Anjali Mehta",
        purpose = "Check-up for Bruno",
        dateLabel = "Tue, 14 Jun",
        timeLabel = "11:30 AM",
    ),
    UpcomingBooking(
        urgencyLabel = "Next week",
        urgencyStyle = UrgencyStyle.AMBER,
        doctorName = "Vaccination booster",
        purpose = "Annual DHPP shot",
        dateLabel = "Wed, 22 Jun",
        timeLabel = "Video",
        isVideo = true,
    ),
)
