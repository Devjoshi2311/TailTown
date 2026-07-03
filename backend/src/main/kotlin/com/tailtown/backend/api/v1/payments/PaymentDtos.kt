package com.tailtown.backend.api.v1.payments

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class VerifyPaymentRequest(
    @field:NotNull(message = "orderId is required")
    val orderId: UUID?,

    @field:NotBlank(message = "razorpayOrderId is required")
    val razorpayOrderId: String,

    @field:NotBlank(message = "razorpayPaymentId is required")
    val razorpayPaymentId: String,

    @field:NotBlank(message = "razorpaySignature is required")
    val razorpaySignature: String
)

data class VerifyBookingPaymentRequest(
    @field:NotNull(message = "bookingId is required")
    val bookingId: UUID?,

    @field:NotBlank(message = "razorpayOrderId is required")
    val razorpayOrderId: String,

    @field:NotBlank(message = "razorpayPaymentId is required")
    val razorpayPaymentId: String,

    @field:NotBlank(message = "razorpaySignature is required")
    val razorpaySignature: String
)
