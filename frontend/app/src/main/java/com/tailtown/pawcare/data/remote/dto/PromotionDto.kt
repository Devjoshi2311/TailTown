package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PromotionResponseDto(
    val id: String,
    val badge: String,
    val title: String,
    val description: String,
    val ctaLabel: String,
    val endsAt: String,
)
