package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VetResponseDto(
    val id: String,
    val displayName: String,
    val specialty: String? = null,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val yearsExperience: Int = 0,
    val homeVisitAvailable: Boolean = false,
    val clinicName: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
)

@Serializable
data class SlotResponseDto(
    @SerialName("id") val id: String,
    @SerialName("startsAt") val startsAt: String,
    @SerialName("endsAt") val endsAt: String,
    @SerialName("status") val status: String,
    @SerialName("serviceType") val serviceType: String = "",
)
