package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetResponseDto(
    val id: String,
    val name: String,
    val breed: String? = null,
    val species: String,
    val ageYears: Int? = null,
    val weightKg: Float? = null,
    val gender: String? = null,
)

@Serializable
data class PrescriptionResponseDto(
    val id: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String? = null,
    val notes: String,
)

@Serializable
data class WeightLogResponseDto(
    val id: String,
    val weightKg: Float,
    val logDate: String,
)

@Serializable
data class LogWeightRequestDto(val weightKg: Float, val logDate: String)

@Serializable
data class CreatePetRequestDto(
    @SerialName("name") val name: String,
    @SerialName("breed") val breed: String? = null,
    @SerialName("species") val species: String,
    @SerialName("gender") val gender: String? = null,
    @SerialName("weightKg") val weightKg: Float? = null,
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
)

@Serializable
data class UpdatePetRequestDto(
    @SerialName("name") val name: String? = null,
    @SerialName("breed") val breed: String? = null,
    @SerialName("species") val species: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("weightKg") val weightKg: Float? = null,
    @SerialName("gender") val gender: String? = null,
)
