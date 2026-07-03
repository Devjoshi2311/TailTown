package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemResponseDto(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
)

@Serializable
data class OrderResponseDto(
    val id: String,
    val status: String,
    val items: List<OrderItemResponseDto>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val deliveryAddress: String,
    val createdAt: String? = null,
)

@Serializable
data class CheckoutRequestDto(
    val deliveryAddress: String = "",
)
