package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.LogWeightRequestDto
import com.tailtown.pawcare.data.repository.HealthRepository
import com.tailtown.pawcare.ui.health.DoseSlot
import com.tailtown.pawcare.ui.health.PrescriptionRecord
import com.tailtown.pawcare.ui.health.WeightPoint
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteHealthRepository @Inject constructor(private val api: ApiService) : HealthRepository {

    private var cachedPetId: String? = null
    private var cachedPrescription: PrescriptionRecord? = null

    private suspend fun primaryPetId(): String? {
        if (cachedPetId != null) return cachedPetId
        cachedPetId = try { api.getPets().data?.firstOrNull()?.id } catch (_: Exception) { null }
        return cachedPetId
    }

    override suspend fun getPrescription(id: String): PrescriptionRecord {
        val petId = primaryPetId() ?: return noDataFallback()
        return try {
            val list = api.getPrescriptions(petId).data ?: return noDataFallback()
            val dto = list.find { it.id == id } ?: list.firstOrNull() ?: return noDataFallback()
            val endDate = dto.endDate?.let { LocalDate.parse(it) }
            val daysLeft = endDate
                ?.let { ChronoUnit.DAYS.between(LocalDate.now(), it).toInt().coerceAtLeast(0) }
                ?: 30
            PrescriptionRecord(
                id = dto.id,
                name = dto.medicationName,
                reason = dto.notes.ifBlank { "As prescribed" },
                daysLeft = daysLeft,
                dosage = dto.dosage,
                frequency = dto.frequency,
                duration = buildDuration(dto.startDate, dto.endDate),
                prescribedBy = "Your Vet",
                refillDaysLeft = (daysLeft - 7).coerceAtLeast(0),
                doses = slotsFromFrequency(dto.frequency),
            ).also { cachedPrescription = it }
        } catch (_: Exception) { noDataFallback() }
    }

    override suspend fun markDose(prescriptionId: String, doseTime: String): PrescriptionRecord {
        return try {
            api.markDose(prescriptionId)
            val updated = cachedPrescription?.copy(
                doses = cachedPrescription!!.doses.map { slot ->
                    if (slot.time == doseTime) slot.copy(taken = true) else slot
                }
            ) ?: getPrescription(prescriptionId)
            cachedPrescription = updated
            updated
        } catch (_: Exception) { cachedPrescription ?: noDataFallback() }
    }

    override suspend fun getWeightHistory(): List<WeightPoint> {
        val petId = primaryPetId() ?: return emptyList()
        return try {
            api.getWeightLogs(petId).data?.map { dto ->
                val date = LocalDate.parse(dto.logDate)
                WeightPoint(
                    monthLabel = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    value = dto.weightKg,
                )
            } ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    override suspend fun logWeight(value: Float): WeightPoint {
        val petId = primaryPetId() ?: return WeightPoint("Now", value)
        return try {
            val today = LocalDate.now()
            api.logWeight(petId, LogWeightRequestDto(weightKg = value, logDate = today.toString()))
            WeightPoint(
                monthLabel = today.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                value = value,
            )
        } catch (_: Exception) { WeightPoint("Now", value) }
    }

    private fun slotsFromFrequency(frequency: String): List<DoseSlot> = when {
        frequency.contains("twice", ignoreCase = true) ->
            listOf(DoseSlot("Morning", false), DoseSlot("Evening", false))
        frequency.contains("three", ignoreCase = true) || frequency.contains("thrice", ignoreCase = true) ->
            listOf(DoseSlot("Morning", false), DoseSlot("Afternoon", false), DoseSlot("Evening", false))
        else -> listOf(DoseSlot("Morning", false))
    }

    private fun buildDuration(start: String, end: String?): String =
        if (end != null) "$start → $end" else "From $start"

    private fun noDataFallback() = PrescriptionRecord(
        id = "none", name = "No prescriptions", reason = "Add a pet and prescription first",
        daysLeft = 0, dosage = "-", frequency = "-", duration = "-",
        prescribedBy = "-", refillDaysLeft = 0, doses = emptyList(),
    )
}
