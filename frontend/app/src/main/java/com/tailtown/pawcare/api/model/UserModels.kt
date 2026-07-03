package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── GET /users/me ──────────────────────────────────────────────────────────
// Also returned inside booking / pet responses as a nested object.

@Serializable
data class UserDto(
    val id: String,
    @SerialName("phone_number") val phoneNumber: String,
    val name: String? = null,
    val email: String? = null,
    @SerialName("profile_photo_url") val profilePhotoUrl: String? = null,
    val pets: List<PetDto> = emptyList(),
    @SerialName("created_at") val createdAt: String,           // ISO-8601
    @SerialName("is_new_user") val isNewUser: Boolean = false,
)

// ── PATCH /users/me ────────────────────────────────────────────────────────

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    @SerialName("profile_photo_url") val profilePhotoUrl: String? = null,
)

// ── POST /users/me/location ────────────────────────────────────────────────
// Called from PermissionsScreen after the user grants location access.

@Serializable
data class SaveLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val area: String? = null,           // reverse-geocoded area name
    val city: String? = null,
)
