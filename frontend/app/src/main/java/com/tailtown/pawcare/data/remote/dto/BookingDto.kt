package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingResponseDto(
    val id: String,
    val petId: String,
    val vetId: String,
    val slotId: String,
    val serviceType: String,
    val visitType: String,
    val scheduledStart: String,
    val scheduledEnd: String,
    val status: String,
    val amount: Double = 0.0,
    val currency: String = "INR",
    val version: Long = 0,
    val notes: String? = null,
    // Only present while status == "PENDING_PAYMENT" — what the client needs to open Razorpay Checkout.
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val amountInPaise: Long? = null,
)

@Serializable
data class CreateBookingRequestDto(
    val petId: String,
    val vetId: String,
    val slotId: String,
    val serviceType: String = "CONSULTATION",
    val visitType: String = "CLINIC",
    val notes: String? = null,
)

@Serializable
data class CancelBookingRequestDto(
    val reason: String = "Cancelled by user",
    val version: Long,
)

@Serializable
data class VerifyBookingPaymentRequestDto(
    val bookingId: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String,
)
