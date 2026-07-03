package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionResponseDto(
    val id: String,
    val productName: String,
    val variantLabel: String,
    val pricePerCycle: Double,
    val isActive: Boolean,
    val nextDelivery: String,
)
