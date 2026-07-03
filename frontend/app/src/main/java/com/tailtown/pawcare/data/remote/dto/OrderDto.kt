package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemResponseDto(
    val productId: String? = null,
    val sku: String = "",
    val productName: String = "",
    val productImageUrl: String? = null,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val lineTotal: Double = 0.0,
)

@Serializable
data class OrderResponseDto(
    val id: String,
    val orderNumber: String = "",
    val status: String = "",
    val paymentStatus: String = "",
    val items: List<OrderItemResponseDto> = emptyList(),
    val subtotal: Double = 0.0,
    val discountTotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val taxTotal: Double = 0.0,
    val grandTotal: Double = 0.0,
    val currency: String = "INR",
    val deliveryAddressSnapshot: String = "",
    val placedAt: String? = null,
    val version: Long = 0,
    // Only present while paymentStatus == "PENDING" — what the client needs to open Razorpay Checkout.
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val amountInPaise: Long? = null,
)

@Serializable
data class VerifyPaymentRequestDto(
    val orderId: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String,
)
