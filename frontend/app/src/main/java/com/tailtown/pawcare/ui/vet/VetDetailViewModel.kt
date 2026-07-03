package com.tailtown.pawcare.ui.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razorpay.Checkout
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.dto.CreateBookingRequestDto
import com.tailtown.pawcare.data.remote.dto.CreatePetRequestDto
import com.tailtown.pawcare.data.remote.dto.VerifyBookingPaymentRequestDto
import com.tailtown.pawcare.data.repository.VetRepository
import com.tailtown.pawcare.payment.RazorpayResult
import com.tailtown.pawcare.payment.RazorpayResultBridge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

sealed class ReservePaymentUiState {
    data object Idle : ReservePaymentUiState()
    data class AwaitingPayment(
        val bookingId: String,
        val razorpayOrderId: String,
        val keyId: String,
        val amountPaise: Long,
        val currency: String,
    ) : ReservePaymentUiState()
    data object Verifying : ReservePaymentUiState()
    data class Success(val bookingId: String) : ReservePaymentUiState()
    data object Pending : ReservePaymentUiState()
    data class Failed(val reason: String, val cancelled: Boolean) : ReservePaymentUiState()
}

@HiltViewModel
class VetDetailViewModel @Inject constructor(
    private val api: ApiService,
    private val vetRepository: VetRepository,
    private val razorpayResultBridge: RazorpayResultBridge,
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

    private val _paymentState = MutableStateFlow<ReservePaymentUiState>(ReservePaymentUiState.Idle)
    val paymentState: StateFlow<ReservePaymentUiState> = _paymentState.asStateFlow()

    private var petId: String? = null
    private var currentVetId: String? = null

    // Cached so "retry" can reopen Checkout against the same Razorpay order instead of a new booking.
    private var pendingPayment: ReservePaymentUiState.AwaitingPayment? = null

    // slotGrid[dateIdx][timeIdx] = slotId  (exact backend IDs, sorted by time)
    private var slotGrid: List<List<String>> = emptyList()
    // timeLabelGrid[dateIdx][timeIdx] = formatted display string ("10:00 AM")
    private var timeLabelGrid: List<List<String>> = emptyList()

    init {
        viewModelScope.launch {
            razorpayResultBridge.results.collect { result ->
                when (result) {
                    is RazorpayResult.Success -> {
                        val myOrderId = pendingPayment?.razorpayOrderId ?: return@collect
                        if (result.razorpayOrderId != null && result.razorpayOrderId != myOrderId) return@collect
                        onRazorpaySuccess(result)
                    }
                    is RazorpayResult.Failure -> {
                        if (pendingPayment == null) return@collect
                        onRazorpayError(result)
                    }
                }
            }
        }
    }

    fun initialize(vetId: String) {
        currentVetId = vetId
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

    /** Creates the booking as PENDING_PAYMENT and, if a gateway order came back, moves to AwaitingPayment. */
    fun reserve(vetId: String) {
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
                if (pid == null || slotId == null) {
                    _slotState.update { it.copy(isBooking = false) }
                    return@launch
                }
                val booking = api.createBooking(
                    CreateBookingRequestDto(
                        petId = pid,
                        vetId = vetId,
                        slotId = slotId,
                        serviceType = "CONSULTATION",
                        visitType = "CLINIC",
                    ),
                    idempotencyKey = "$pid-$slotId",
                ).data

                _slotState.update { it.copy(isBooking = false) }

                val razorpayOrderId = booking?.razorpayOrderId
                val keyId = booking?.razorpayKeyId
                val amountPaise = booking?.amountInPaise
                if (booking != null && booking.status == "PENDING_PAYMENT" && razorpayOrderId != null && keyId != null && amountPaise != null) {
                    _paymentState.value = ReservePaymentUiState.AwaitingPayment(
                        bookingId = booking.id,
                        razorpayOrderId = razorpayOrderId,
                        keyId = keyId,
                        amountPaise = amountPaise,
                        currency = booking.currency,
                    ).also { pendingPayment = it }
                } else if (booking != null) {
                    _paymentState.value = ReservePaymentUiState.Success(booking.id)
                } else {
                    _paymentState.value = ReservePaymentUiState.Failed("Couldn't reserve this slot. Please try again.", cancelled = false)
                }
            } catch (e: Exception) {
                val body = if (e is HttpException) e.response()?.errorBody()?.string() else null
                android.util.Log.e("VetReserve", "EXCEPTION: ${e.message} body=$body", e)
                _slotState.update { it.copy(isBooking = false) }
                _paymentState.value = ReservePaymentUiState.Failed("Couldn't reserve this slot. Please try again.", cancelled = false)
            }
        }
    }

    /** Reopens Checkout on the same booking if one exists, otherwise starts a fresh reservation. */
    fun retryPayment() {
        val awaiting = pendingPayment
        val vetId = currentVetId
        if (awaiting != null) _paymentState.value = awaiting
        else if (vetId != null) reserve(vetId)
    }

    fun dismissPaymentState() {
        _paymentState.value = ReservePaymentUiState.Idle
        pendingPayment = null
    }

    private fun onRazorpaySuccess(result: RazorpayResult.Success) {
        val bookingId = pendingPayment?.bookingId ?: return
        val razorpayOrderId = result.razorpayOrderId ?: pendingPayment?.razorpayOrderId ?: return
        val signature = result.razorpaySignature ?: return
        viewModelScope.launch {
            _paymentState.value = ReservePaymentUiState.Verifying
            try {
                val booking = api.verifyBookingPayment(
                    VerifyBookingPaymentRequestDto(
                        bookingId = bookingId,
                        razorpayOrderId = razorpayOrderId,
                        razorpayPaymentId = result.razorpayPaymentId,
                        razorpaySignature = signature,
                    )
                ).data
                _paymentState.value = if (booking?.status == "CONFIRMED") {
                    ReservePaymentUiState.Success(bookingId)
                } else {
                    ReservePaymentUiState.Pending
                }
            } catch (e: HttpException) {
                // Backend was reached and explicitly declined it — a real failure.
                _paymentState.value = ReservePaymentUiState.Failed(
                    reason = "We couldn't verify this payment. If any amount was deducted, it will be refunded.",
                    cancelled = false,
                )
            } catch (e: IOException) {
                // Couldn't reach the backend — the payment may well have succeeded. Never guess "failed"
                // here; the webhook/reconciliation job is the source of truth, so wait and poll for it.
                _paymentState.value = ReservePaymentUiState.Pending
                pollForResolution(bookingId)
            }
        }
    }

    private fun onRazorpayError(result: RazorpayResult.Failure) {
        val cancelled = result.code == Checkout.PAYMENT_CANCELED
        _paymentState.value = ReservePaymentUiState.Failed(
            reason = if (cancelled) "Payment cancelled." else (result.description ?: "Payment failed."),
            cancelled = cancelled,
        )
    }

    private fun pollForResolution(bookingId: String) {
        viewModelScope.launch {
            repeat(6) {
                delay(3000)
                try {
                    val booking = api.getBooking(bookingId).data
                    when (booking?.status) {
                        "CONFIRMED" -> {
                            _paymentState.value = ReservePaymentUiState.Success(bookingId)
                            return@launch
                        }
                        "PAYMENT_FAILED" -> {
                            _paymentState.value = ReservePaymentUiState.Failed("Payment could not be completed.", cancelled = false)
                            return@launch
                        }
                    }
                } catch (_: Exception) {
                    // Transient — keep polling, settle on the Pending copy if the window runs out.
                }
            }
        }
    }
}
