package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponseDto(
    val id: String,
    val parentId: String? = null,
    val name: String,
    val slug: String,
    val description: String? = null,
    val sortOrder: Int = 0,
    val imageUrl: String? = null,
)

@Serializable
data class ProductResponseDto(
    val id: String,
    val name: String,
    val subtitle: String = "",
    val description: String = "",
    val categoryId: String? = null,
    val brand: String = "",
    val price: Double,
    val mrp: Double,
    val imageUrl: String? = null,
    val stockQty: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isBestseller: Boolean = false,
    val discountPct: Int? = null,
)

@Serializable
data class CartItemResponseDto(
    val id: String,
    val productId: String,
    val productName: String = "",
    val imageUrl: String? = null,
    val price: Double = 0.0,
    val quantity: Int,
    val lineTotal: Double,
    val version: Long = 0,
)

@Serializable
data class CartResponseDto(
    val items: List<CartItemResponseDto>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
)

@Serializable
data class AddToCartRequestDto(val productId: String, val quantity: Int = 1)

@Serializable
data class UpdateCartItemRequestDto(val quantity: Int, val version: Long)

@Serializable
data class CreateOrderRequestDto(
    @SerialName("addressId") val addressId: String,
    @SerialName("notes") val notes: String? = null,
)

@Serializable
data class PauseSubscriptionRequestDto(
    @SerialName("pausedUntil") val pausedUntil: String? = null,
    @SerialName("version") val version: Long = 0,
)

@Serializable
data class ResumeSubscriptionRequestDto(
    @SerialName("version") val version: Long = 0,
)

@Serializable
data class CancelSubscriptionRequestDto(
    @SerialName("reason") val reason: String,
    @SerialName("version") val version: Long,
)

@Serializable
data class CreateSubscriptionRequestDto(
    @SerialName("productId") val productId: String,
    @SerialName("addressId") val addressId: String,
    @SerialName("quantity") val quantity: Int = 1,
    @SerialName("cadence") val cadence: String,
)

@Serializable
data class PushTokenRequestDto(
    @SerialName("deviceId") val deviceId: String,
    @SerialName("token") val token: String,
    @SerialName("platform") val platform: String = "ANDROID",
)

@Serializable
data class ClaimReferralRequestDto(
    @SerialName("referralCode") val referralCode: String,
)
