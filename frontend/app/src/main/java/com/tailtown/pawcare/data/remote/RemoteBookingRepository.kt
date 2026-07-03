package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.BookingResponseDto
import com.tailtown.pawcare.data.remote.dto.CancelBookingRequestDto
import com.tailtown.pawcare.data.remote.dto.VetResponseDto
import com.tailtown.pawcare.ui.vet.UpcomingBooking
import com.tailtown.pawcare.ui.vet.UrgencyStyle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteBookingRepository @Inject constructor(private val api: ApiService) {

    suspend fun getBookings(): List<UpcomingBooking> {
        val bookings = api.getBookings().data ?: return emptyList()
        if (bookings.isEmpty()) return emptyList()
        val vets = try { api.getVets().data ?: emptyList() } catch (_: Exception) { emptyList() }
        val vetNameById = vets.associate { it.id to it.displayName }
        return bookings.map { it.toBooking(vetNameById[it.vetId] ?: "Vet Appointment") }
    }

    suspend fun cancelBooking(bookingId: String, version: Long) {
        api.cancelBooking(bookingId, CancelBookingRequestDto(version = version))
    }
}

private fun BookingResponseDto.toBooking(vetName: String): UpcomingBooking {
    android.util.Log.d("BookingMap", "scheduledStart raw=$scheduledStart status=$status")
    val datePart = scheduledStart.take(10)
    val timePart = if (scheduledStart.length >= 16) scheduledStart.substring(11, 16) else ""
    val formattedDate = runCatching {
        val d = java.time.Instant.parse(scheduledStart)
        val local = d.atZone(java.time.ZoneId.of("Asia/Kolkata")).toLocalDate()
        val dayName = local.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        "$dayName, ${local.dayOfMonth} $month"
    }.getOrDefault(datePart)
    val formattedTime = runCatching {
        val d = java.time.Instant.parse(scheduledStart)
        val local = d.atZone(java.time.ZoneId.of("Asia/Kolkata")).toLocalTime()
        val h = local.hour % 12
        val m = local.minute.toString().padStart(2, '0')
        val amPm = if (local.hour < 12) "AM" else "PM"
        "${if (h == 0) 12 else h}:$m $amPm"
    }.getOrDefault(timePart)
    android.util.Log.d("BookingMap", "formattedDate=$formattedDate formattedTime=$formattedTime")
    val epochMs = runCatching { java.time.Instant.parse(scheduledStart).toEpochMilli() }.getOrDefault(0L)
    return UpcomingBooking(
        bookingId = id,
        status = status,
        version = version,
        scheduledStartEpoch = epochMs,
        urgencyLabel = when (status) {
            "CONFIRMED" -> "Upcoming"
            "COMPLETED" -> "Completed"
            "CANCELLED" -> "Cancelled"
            else -> status.lowercase().replaceFirstChar { it.uppercase() }
        },
        urgencyStyle = if (status == "CONFIRMED") UrgencyStyle.CORAL else UrgencyStyle.AMBER,
        doctorName = vetName,
        purpose = serviceType.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
        dateLabel = formattedDate,
        timeLabel = formattedTime,
        isVideo = visitType == "VIDEO",
    )
}
