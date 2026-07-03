package com.tailtown.pawcare.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razorpay.Checkout
import com.tailtown.pawcare.data.repository.ShopRepository
import com.tailtown.pawcare.payment.RazorpayResult
import com.tailtown.pawcare.payment.RazorpayResultBridge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class CheckoutUiState {
    data object Idle : CheckoutUiState()
    data object PlacingOrder : CheckoutUiState()
    data class AwaitingPayment(
        val orderId: String,
        val razorpayOrderId: String,
        val keyId: String,
        val amountPaise: Long,
        val currency: String,
    ) : CheckoutUiState()
    data object Verifying : CheckoutUiState()
    data class Success(val orderNumber: String, val amount: Int) : CheckoutUiState()
    data object Pending : CheckoutUiState()
    data class Failed(val reason: String, val cancelled: Boolean) : CheckoutUiState()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val razorpayResultBridge: RazorpayResultBridge,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // Cached so "retry" can reopen Checkout against the same Razorpay order instead of creating a new one.
    private var pendingPayment: CheckoutUiState.AwaitingPayment? = null
    private var currentOrderId: String? = null

    init {
        viewModelScope.launch {
            razorpayResultBridge.results.collect { result ->
                when (result) {
                    is RazorpayResult.Success -> onRazorpaySuccess(result)
                    is RazorpayResult.Failure -> onRazorpayError(result)
                }
            }
        }
    }

    fun placeOrder(addressId: String) {
        if (_uiState.value == CheckoutUiState.PlacingOrder) return
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.PlacingOrder
            try {
                val order = shopRepository.placeOrder(addressId)
                currentOrderId = order.orderId
                val razorpayOrderId = order.razorpayOrderId
                val keyId = order.razorpayKeyId
                val amountPaise = order.amountInPaise
                _uiState.value = if (order.paymentStatus == "PENDING" && razorpayOrderId != null && keyId != null && amountPaise != null) {
                    CheckoutUiState.AwaitingPayment(
                        orderId = order.orderId,
                        razorpayOrderId = razorpayOrderId,
                        keyId = keyId,
                        amountPaise = amountPaise,
                        currency = order.currency,
                    ).also { pendingPayment = it }
                } else {
                    CheckoutUiState.Success(order.orderNumber, order.grandTotal)
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Failed(
                    reason = "Couldn't place your order. Please try again.",
                    cancelled = false,
                )
            }
        }
    }

    /** Reopens Checkout on the same order if one exists, otherwise places a fresh order. */
    fun retry(addressId: String) {
        val awaiting = pendingPayment
        if (awaiting != null) _uiState.value = awaiting else placeOrder(addressId)
    }

    private fun onRazorpaySuccess(result: RazorpayResult.Success) {
        val orderId = currentOrderId ?: return
        val razorpayOrderId = result.razorpayOrderId ?: pendingPayment?.razorpayOrderId ?: return
        val signature = result.razorpaySignature ?: return
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Verifying
            try {
                val order = shopRepository.verifyPayment(orderId, razorpayOrderId, result.razorpayPaymentId, signature)
                _uiState.value = if (order.paymentStatus == "PAID") {
                    CheckoutUiState.Success(order.orderNumber, order.grandTotal)
                } else {
                    CheckoutUiState.Pending
                }
            } catch (e: HttpException) {
                // Backend was reached and explicitly declined it (bad signature / amount mismatch) — a real failure.
                _uiState.value = CheckoutUiState.Failed(
                    reason = "We couldn't verify this payment. If any amount was deducted, it will be refunded.",
                    cancelled = false,
                )
            } catch (e: IOException) {
                // Couldn't even reach the backend — the payment may well have succeeded. Never guess "failed"
                // here; the webhook/reconciliation job is the source of truth, so wait and poll for it.
                _uiState.value = CheckoutUiState.Pending
                pollForResolution(orderId)
            }
        }
    }

    private fun onRazorpayError(result: RazorpayResult.Failure) {
        val cancelled = result.code == Checkout.PAYMENT_CANCELED
        _uiState.value = CheckoutUiState.Failed(
            reason = if (cancelled) "Payment cancelled." else (result.description ?: "Payment failed."),
            cancelled = cancelled,
        )
    }

    private fun pollForResolution(orderId: String) {
        viewModelScope.launch {
            repeat(6) {
                delay(3000)
                try {
                    val order = shopRepository.getOrder(orderId)
                    when (order.paymentStatus) {
                        "PAID" -> {
                            _uiState.value = CheckoutUiState.Success(order.orderNumber, order.grandTotal)
                            return@launch
                        }
                        "FAILED" -> {
                            _uiState.value = CheckoutUiState.Failed(
                                reason = "Payment could not be completed.",
                                cancelled = false,
                            )
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
