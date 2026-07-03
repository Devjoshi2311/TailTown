package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseDto(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val deepLink: String? = null,
    val createdAt: String,
)

@Serializable
data class NotifPrefsResponseDto(
    val appointments: Boolean,
    val medications: Boolean,
    val orders: Boolean,
    val promos: Boolean,
)

@Serializable
data class NotifPrefsRequestDto(
    val appointments: Boolean,
    val medications: Boolean,
    val orders: Boolean,
    val promos: Boolean,
)
