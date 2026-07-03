package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVaccineRequestDto(
    @SerialName("vaccineName") val vaccineName: String,
    @SerialName("doseLabel") val doseLabel: String? = null,
    @SerialName("dueDate") val dueDate: String? = null,
    @SerialName("administeredDate") val administeredDate: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("providerName") val providerName: String? = null,
    @SerialName("certificateUrl") val certificateUrl: String? = null,
    @SerialName("notes") val notes: String? = null,
)

@Serializable
data class VaccineResponseDto(
    @SerialName("id") val id: String,
    @SerialName("petId") val petId: String,
    @SerialName("vaccineName") val vaccineName: String,
    @SerialName("doseLabel") val doseLabel: String? = null,
    @SerialName("dueDate") val dueDate: String? = null,
    @SerialName("administeredDate") val administeredDate: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("providerName") val providerName: String? = null,
    @SerialName("certificateUrl") val certificateUrl: String? = null,
    @SerialName("version") val version: Long,
)
