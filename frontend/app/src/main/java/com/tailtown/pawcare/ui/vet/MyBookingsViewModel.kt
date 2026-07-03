package com.tailtown.pawcare.ui.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.remote.RemoteBookingRepository
import com.tailtown.pawcare.data.remote.dto.CancelBookingRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingRepo: RemoteBookingRepository,
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<UpcomingBooking>>(emptyList())
    val bookings: StateFlow<List<UpcomingBooking>> = _bookings.asStateFlow()

    // No eager init — MyBookingsScreen already triggers refresh() itself via LaunchedEffect,
    // so fetching here too was a redundant duplicate call on every cold start.

    fun refresh() {
        viewModelScope.launch {
            try { _bookings.value = bookingRepo.getBookings() } catch (_: Exception) {}
        }
    }

    fun cancelBooking(bookingId: String, version: Long) {
        viewModelScope.launch {
            try {
                bookingRepo.cancelBooking(bookingId, version)
                refresh()
            } catch (_: Exception) {}
        }
    }
}
