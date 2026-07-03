package com.tailtown.pawcare.ui.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.dto.CreateBookingRequestDto
import com.tailtown.pawcare.data.remote.dto.CreatePetRequestDto
import com.tailtown.pawcare.data.repository.VetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class VetDetailViewModel @Inject constructor(
    private val api: ApiService,
    private val vetRepository: VetRepository,
) : ViewModel() {

    data class DateChip(val dayLabel: String, val dayNum: Int, val monthLabel: String)

    data class SlotSelectionState(
        val isLoading: Boolean = true,
        val availableDates: List<DateChip> = emptyList(),
        val timeSlotsForDate: List<String> = emptyList(),
        val selectedDateIdx: Int = 0,
        val selectedTimeIdx: Int = 0,
        val isBooking: Boolean = false,
        val bookingSuccess: Boolean = false,
    )

    private val _slotState = MutableStateFlow(SlotSelectionState())
    val slotState: StateFlow<SlotSelectionState> = _slotState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private var petId: String? = null

    // slotGrid[dateIdx][timeIdx] = slotId  (exact backend IDs, sorted by time)
    private var slotGrid: List<List<String>> = emptyList()
    // timeLabelGrid[dateIdx][timeIdx] = formatted display string ("10:00 AM")
    private var timeLabelGrid: List<List<String>> = emptyList()

    fun initialize(vetId: String) {
        viewModelScope.launch {
            loadPet()
            loadSlots(vetId)
        }
    }

    private suspend fun loadPet() {
        try { petId = api.getPets().data?.firstOrNull()?.id } catch (_: Exception) {}
    }

    private suspend fun loadSlots(vetId: String) {
        _slotState.update { it.copy(isLoading = true) }
        try {
            val now = Instant.now()
            val twoWeeksLater = now.plus(14, ChronoUnit.DAYS)
            val all = api.getVetSlots(vetId, now.toString(), twoWeeksLater.toString()).data
                ?: emptyList()

            val ist = ZoneId.of("Asia/Kolkata")
            val available = all.filter { it.status == "AVAILABLE" }

            // Group by IST date, sort within each date by IST time
            val byDate = available
                .groupBy { Instant.parse(it.startsAt).atZone(ist).toLocalDate() }
                .entries
                .sortedBy { it.key }

            val dateChips = byDate.map { (date, _) ->
                DateChip(
                    dayLabel = date.dayOfWeek.name.take(3).replaceFirstChar { it.uppercase() },
                    dayNum = date.dayOfMonth,
                    monthLabel = date.month.name.take(3).replaceFirstChar { it.uppercase() },
                )
            }

            slotGrid = byDate.map { (_, slots) ->
                slots.sortedBy { Instant.parse(it.startsAt).atZone(ist).toLocalTime() }.map { it.id }
            }

            timeLabelGrid = byDate.map { (_, slots) ->
                slots.sortedBy { Instant.parse(it.startsAt).atZone(ist).toLocalTime() }.map { slot ->
                    formatTime(Instant.parse(slot.startsAt).atZone(ist).toLocalTime())
                }
            }

            _slotState.update {
                it.copy(
                    isLoading = false,
                    availableDates = dateChips,
                    timeSlotsForDate = timeLabelGrid.firstOrNull() ?: emptyList(),
                    selectedDateIdx = 0,
                    selectedTimeIdx = 0,
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("VetDetail", "loadSlots failed: ${e.message}", e)
            _slotState.update { it.copy(isLoading = false) }
        }
    }

    private fun formatTime(lt: LocalTime): String {
        val h = lt.hour % 12
        val m = lt.minute.toString().padStart(2, '0')
        val amPm = if (lt.hour < 12) "AM" else "PM"
        return "${if (h == 0) 12 else h}:$m $amPm"
    }

    fun selectDate(idx: Int) {
        _slotState.update {
            it.copy(
                selectedDateIdx = idx,
                selectedTimeIdx = 0,
                timeSlotsForDate = timeLabelGrid.getOrElse(idx) { emptyList() },
            )
        }
    }

    fun selectTime(idx: Int) {
        _slotState.update { it.copy(selectedTimeIdx = idx) }
    }

    fun toggleSave(vetId: String) {
        viewModelScope.launch { _isSaved.value = vetRepository.toggleSave(vetId) }
    }

    fun reserve(vetId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _slotState.update { it.copy(isBooking = true) }
            try {
                var pid = petId
                if (pid == null) {
                    pid = api.createPet(CreatePetRequestDto(name = "My Pet", species = "Dog")).data?.id
                    petId = pid
                }
                val state = _slotState.value
                val slotId = slotGrid.getOrNull(state.selectedDateIdx)?.getOrNull(state.selectedTimeIdx)
                android.util.Log.d("VetReserve", "booking: pid=$pid slotId=$slotId dateIdx=${state.selectedDateIdx} timeIdx=${state.selectedTimeIdx}")
                if (pid != null && slotId != null) {
                    api.createBooking(
                        CreateBookingRequestDto(
                            petId = pid,
                            vetId = vetId,
                            slotId = slotId,
                            serviceType = "CONSULTATION",
                            visitType = "CLINIC",
                        ),
                        idempotencyKey = "$pid-$slotId",
                    )
                    android.util.Log.d("VetReserve", "SUCCESS")
                    _slotState.update { it.copy(isBooking = false, bookingSuccess = true) }
                    onSuccess()
                } else {
                    android.util.Log.w("VetReserve", "SKIPPED: pid=$pid slotId=$slotId")
                    _slotState.update { it.copy(isBooking = false) }
                }
            } catch (e: Exception) {
                val body = if (e is retrofit2.HttpException) e.response()?.errorBody()?.string() else null
                android.util.Log.e("VetReserve", "EXCEPTION: ${e.message} body=$body", e)
                _slotState.update { it.copy(isBooking = false) }
            }
        }
    }
}
