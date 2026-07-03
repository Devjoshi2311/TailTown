package com.tailtown.backend.api.v1.notifications

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val type: String,
    val title: String,
    val body: String,
    val deepLink: String?,
    val priority: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val version: Long
)

data class MarkReadRequest(
    val version: Long? = null
)

data class NotificationPreferencesResponse(
    val appointments: Boolean,
    val medications: Boolean,
    val orders: Boolean,
    val promos: Boolean,
    val chat: Boolean,
    val version: Long
)

data class UpdatePreferencesRequest(
    val appointments: Boolean = true,
    val medications: Boolean = true,
    val orders: Boolean = true,
    val promos: Boolean = true,
    val chat: Boolean = true,
    @field:NotNull val version: Long?
)

data class PushTokenRequest(
    @field:NotBlank val deviceId: String?,
    @field:NotBlank val token: String?,
    val platform: String = "ANDROID"
)
