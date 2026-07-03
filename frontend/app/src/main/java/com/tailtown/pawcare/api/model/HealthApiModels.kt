package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VaccineStatusDto {
    @SerialName("due") DUE,
    @SerialName("upcoming") UPCOMING,
    @SerialName("completed") COMPLETED,
}

@Serializable
data class VaccineRecordDto(
    val id: String,
    val name: String,
    val frequency: String,
    val status: VaccineStatusDto,
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("given_date") val givenDate: String? = null,
    val vet: String? = null,
)

@Serializable
data class VaccinationData(val records: List<VaccineRecordDto>)

@Serializable
enum class TimelineEntryTypeDto {
    @SerialName("check_up") CHECK_UP,
    @SerialName("prescription") PRESCRIPTION,
    @SerialName("vaccine") VACCINE,
}

@Serializable
data class TimelineEntryDto(
    val id: String,
    val type: TimelineEntryTypeDto,
    val title: String,
    val subtitle: String,
    val date: String,
    val note: String? = null,
)

@Serializable
data class MedicalTimelineData(val entries: List<TimelineEntryDto>)

@Serializable
data class DoseSlotDto(val time: String, val taken: Boolean)

@Serializable
data class PrescriptionDto(
    val id: String,
    val name: String,
    val reason: String,
    @SerialName("days_left") val daysLeft: Int,
    val dosage: String,
    val frequency: String,
    val duration: String,
    @SerialName("prescribed_by") val prescribedBy: String,
    @SerialName("prescribed_date") val prescribedDate: String,
    @SerialName("refill_days_left") val refillDaysLeft: Int,
    val doses: List<DoseSlotDto>,
)

@Serializable
data class PrescriptionData(val prescription: PrescriptionDto)

@Serializable
data class WeightPointDto(val date: String, val value: Float)

@Serializable
data class WeightHistoryData(
    val current: Float,
    @SerialName("healthy_min") val healthyMin: Float,
    @SerialName("healthy_max") val healthyMax: Float,
    val history: List<WeightPointDto>,
)

@Serializable
data class LogWeightRequest(val weight: Float, val date: String)

@Serializable
data class WeightData(val history: WeightHistoryData)

@Serializable
data class AddVaccineRecordRequest(
    val name: String,
    @SerialName("given_date") val givenDate: String,
    val vet: String? = null,
    val notes: String? = null,
)
