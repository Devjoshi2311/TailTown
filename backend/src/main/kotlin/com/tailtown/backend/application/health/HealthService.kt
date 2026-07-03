package com.tailtown.backend.application.health

import com.tailtown.backend.infrastructure.persistence.health.PrescriptionDoseEntity
import com.tailtown.backend.infrastructure.persistence.health.PrescriptionDoseRepository
import com.tailtown.backend.infrastructure.persistence.health.PrescriptionEntity
import com.tailtown.backend.infrastructure.persistence.health.PrescriptionRepository
import com.tailtown.backend.infrastructure.persistence.health.VaccineEntity
import com.tailtown.backend.infrastructure.persistence.health.VaccineRepository
import com.tailtown.backend.infrastructure.persistence.health.WeightRecordEntity
import com.tailtown.backend.infrastructure.persistence.health.WeightRecordRepository
import com.tailtown.backend.infrastructure.persistence.pets.PetRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.WeightRecordDuplicateException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional
class HealthService(
    private val weightRecordRepository: WeightRecordRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val prescriptionDoseRepository: PrescriptionDoseRepository,
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
) {

    // ── Weight Records ────────────────────────────────────────────────────────

    fun getWeightRecords(userId: UUID, petId: UUID): List<WeightRecordEntity> {
        verifyPetOwnership(userId, petId)
        return weightRecordRepository.findAllByPetIdAndUserIdAndDeletedAtIsNull(petId, userId)
    }

    fun addWeightRecord(
        userId: UUID,
        petId: UUID,
        weightKg: BigDecimal,
        recordedOn: LocalDate,
        notes: String?,
    ): WeightRecordEntity {
        verifyPetOwnership(userId, petId)
        if (weightRecordRepository.existsByPetIdAndRecordedOnAndDeletedAtIsNull(petId, recordedOn)) {
            throw WeightRecordDuplicateException(petId, recordedOn)
        }
        val entity = WeightRecordEntity(
            petId = petId,
            userId = userId,
            weightKg = weightKg,
            recordedOn = recordedOn,
            notes = notes,
        )
        return weightRecordRepository.save(entity)
    }

    // ── Prescriptions ─────────────────────────────────────────────────────────

    fun getPrescriptions(userId: UUID, petId: UUID, status: String?): List<PrescriptionEntity> {
        verifyPetOwnership(userId, petId)
        val all = prescriptionRepository
            .findAllByPetIdAndUserIdAndDeletedAtIsNullOrderByStartDateDesc(petId, userId)
        return if (status != null) all.filter { it.status == status } else all
    }

    fun createPrescription(
        userId: UUID,
        petId: UUID,
        req: CreatePrescriptionRequest,
    ): PrescriptionEntity {
        verifyPetOwnership(userId, petId)
        val entity = PrescriptionEntity(
            petId = petId,
            userId = userId,
            medicationName = req.medicationName,
            dosage = req.dosage,
            frequency = req.frequency,
            instructions = req.instructions,
            startDate = req.startDate,
            endDate = req.endDate,
            documentUrl = req.documentUrl,
        )
        return prescriptionRepository.save(entity)
    }

    fun markDose(
        userId: UUID,
        prescriptionId: UUID,
        takenAt: Instant?,
        note: String?,
    ): PrescriptionDoseEntity {
        prescriptionRepository.findByIdAndUserIdAndDeletedAtIsNull(prescriptionId, userId)
            .orElseThrow { ResourceNotFoundException("Prescription", prescriptionId) }
        val dose = PrescriptionDoseEntity(
            prescriptionId = prescriptionId,
            userId = userId,
            takenAt = takenAt ?: Instant.now(),
            note = note,
        )
        return prescriptionDoseRepository.save(dose)
    }

    // ── Vaccines ──────────────────────────────────────────────────────────────

    fun getVaccines(userId: UUID, petId: UUID, status: String?): List<VaccineEntity> {
        verifyPetOwnership(userId, petId)
        val all = vaccineRepository.findAllByPetIdAndUserIdAndDeletedAtIsNull(petId, userId)
        return if (status != null) all.filter { it.status == status } else all
    }

    fun addVaccine(
        userId: UUID,
        petId: UUID,
        req: CreateVaccineRequest,
    ): VaccineEntity {
        verifyPetOwnership(userId, petId)
        val entity = VaccineEntity(
            petId = petId,
            userId = userId,
            vaccineName = req.vaccineName,
            doseLabel = req.doseLabel,
            dueDate = req.dueDate,
            administeredDate = req.administeredDate,
            status = req.status ?: "DUE",
            providerName = req.providerName,
            certificateUrl = req.certificateUrl,
            notes = req.notes,
        )
        return vaccineRepository.save(entity)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun verifyPetOwnership(userId: UUID, petId: UUID) {
        if (!petRepository.existsByIdAndUserIdAndDeletedAtIsNull(petId, userId)) {
            throw ResourceNotFoundException("Pet", petId)
        }
    }
}
