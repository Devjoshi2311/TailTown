package com.tailtown.pawcare.payment

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class RazorpayResult {
    data class Success(
        val razorpayPaymentId: String,
        val razorpayOrderId: String?,
        val razorpaySignature: String?,
    ) : RazorpayResult()

    data class Failure(val code: Int, val description: String?) : RazorpayResult()
}

/**
 * Razorpay's Checkout SDK requires the *launching Activity itself* to implement
 * PaymentResultWithDataListener, so the callback can't go straight to a ViewModel.
 * MainActivity forwards both callbacks here; CheckoutViewModel collects [results].
 */
@Singleton
class RazorpayResultBridge @Inject constructor() {

    private val _results = MutableSharedFlow<RazorpayResult>(extraBufferCapacity = 1)
    val results: SharedFlow<RazorpayResult> = _results.asSharedFlow()

    fun emitSuccess(razorpayPaymentId: String, razorpayOrderId: String?, razorpaySignature: String?) {
        _results.tryEmit(RazorpayResult.Success(razorpayPaymentId, razorpayOrderId, razorpaySignature))
    }

    fun emitFailure(code: Int, description: String?) {
        _results.tryEmit(RazorpayResult.Failure(code, description))
    }
}
