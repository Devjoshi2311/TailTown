package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.health.PrescriptionRecord
import com.tailtown.pawcare.ui.health.WeightPoint

interface HealthRepository {
    suspend fun getPrescription(id: String): PrescriptionRecord
    suspend fun markDose(prescriptionId: String, doseTime: String): PrescriptionRecord
    suspend fun getWeightHistory(): List<WeightPoint>
    suspend fun logWeight(value: Float): WeightPoint
}
