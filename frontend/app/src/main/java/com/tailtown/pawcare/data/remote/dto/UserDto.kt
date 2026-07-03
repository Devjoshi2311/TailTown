package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val referralCode: String? = null,
)

@Serializable
data class UpdateProfileRequestDto(val name: String, val phone: String = "")

@Serializable
data class AddressResponseDto(
    val id: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pincode: String,
    val isDefault: Boolean,
)

@Serializable
data class AddressRequestDto(
    @SerialName("label") val label: String,
    @SerialName("street") val street: String,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("pincode") val pincode: String,
    @SerialName("isDefault") val isDefault: Boolean = false,
)

@Serializable
data class PaymentMethodResponseDto(
    val id: String,
    val type: String,
    val label: String,
    val masked: String,
    val isDefault: Boolean,
)

@Serializable
data class ReferralResponseDto(
    val code: String,
    val referrerReward: Int,
    val refereeReward: Int,
    val referralsMade: Int,
    val rewardsEarned: Int,
)
