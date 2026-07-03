package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.health.PrescriptionRecord
import com.tailtown.pawcare.ui.health.WeightPoint
import com.tailtown.pawcare.ui.health.samplePrescription
import com.tailtown.pawcare.ui.health.sampleWeightPoints

class StubHealthRepository : HealthRepository {
    private var prescription = samplePrescription
    private val weights = sampleWeightPoints.toMutableList()

    override suspend fun getPrescription(id: String) = prescription
    override suspend fun markDose(prescriptionId: String, doseTime: String): PrescriptionRecord {
        prescription = prescription.copy(
            doses = prescription.doses.map { if (it.time == doseTime) it.copy(taken = true) else it }
        )
        return prescription
    }
    override suspend fun getWeightHistory() = weights.toList()
    override suspend fun logWeight(value: Float): WeightPoint {
        val point = WeightPoint("Jun", value)
        weights[weights.lastIndex] = point
        return point
    }
}
