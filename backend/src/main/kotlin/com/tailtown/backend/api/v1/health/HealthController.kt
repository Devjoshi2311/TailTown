package com.tailtown.backend.api.v1.health

import com.tailtown.backend.application.health.AddWeightRequest
import com.tailtown.backend.application.health.CreatePrescriptionRequest
import com.tailtown.backend.application.health.CreateVaccineRequest
import com.tailtown.backend.application.health.DoseResponse
import com.tailtown.backend.application.health.HealthService
import com.tailtown.backend.application.health.MarkDoseRequest
import com.tailtown.backend.application.health.PrescriptionResponse
import com.tailtown.backend.application.health.VaccineResponse
import com.tailtown.backend.application.health.WeightRecordResponse
import com.tailtown.backend.infrastructure.persistence.health.PrescriptionDoseEntity
import com.tailtown.backend.infrastructure.persistence.health.PrescriptionEntity
import com.tailtown.backend.infrastructure.persistence.health.VaccineEntity
import com.tailtown.backend.infrastructure.persistence.health.WeightRecordEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class HealthController(
    private val healthService: HealthService,
) {

    // ── Weight Records ────────────────────────────────────────────────────────

    @GetMapping("/pets/{petId}/weight-records")
    fun getWeightRecords(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
    ): List<WeightRecordResponse> =
        healthService.getWeightRecords(principal.userId, petId)
            .map { it.toResponse() }

    @PostMapping("/pets/{petId}/weight-records")
    @ResponseStatus(HttpStatus.CREATED)
    fun addWeightRecord(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @Valid @RequestBody req: AddWeightRequest,
    ): WeightRecordResponse =
        healthService.addWeightRecord(
            userId = principal.userId,
            petId = petId,
            weightKg = req.weightKg,
            recordedOn = req.recordedOn,
            notes = req.notes,
        ).toResponse()

    // ── Prescriptions ─────────────────────────────────────────────────────────

    @GetMapping("/pets/{petId}/prescriptions")
    fun getPrescriptions(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @RequestParam(required = false) status: String?,
    ): List<PrescriptionResponse> =
        healthService.getPrescriptions(principal.userId, petId, status)
            .map { it.toResponse() }

    @PostMapping("/pets/{petId}/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPrescription(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @Valid @RequestBody req: CreatePrescriptionRequest,
    ): PrescriptionResponse =
        healthService.createPrescription(
            userId = principal.userId,
            petId = petId,
            req = req,
        ).toResponse()

    // ── Doses ─────────────────────────────────────────────────────────────────

    @PostMapping("/prescriptions/{prescriptionId}/doses")
    @ResponseStatus(HttpStatus.CREATED)
    fun markDose(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable prescriptionId: UUID,
        @RequestBody(required = false) req: MarkDoseRequest?,
    ): DoseResponse =
        healthService.markDose(
            userId = principal.userId,
            prescriptionId = prescriptionId,
            takenAt = req?.takenAt,
            note = req?.note,
        ).toResponse()

    // ── Vaccines ──────────────────────────────────────────────────────────────

    @GetMapping("/pets/{petId}/vaccines")
    fun getVaccines(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @RequestParam(required = false) status: String?,
    ): List<VaccineResponse> =
        healthService.getVaccines(principal.userId, petId, status)
            .map { it.toResponse() }

    @PostMapping("/pets/{petId}/vaccines")
    @ResponseStatus(HttpStatus.CREATED)
    fun addVaccine(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @Valid @RequestBody req: CreateVaccineRequest,
    ): VaccineResponse =
        healthService.addVaccine(
            userId = principal.userId,
            petId = petId,
            req = req,
        ).toResponse()

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun WeightRecordEntity.toResponse() = WeightRecordResponse(
        id = id,
        petId = petId,
        weightKg = weightKg,
        recordedOn = recordedOn,
        source = source,
        notes = notes,
        version = version,
    )

    private fun PrescriptionEntity.toResponse() = PrescriptionResponse(
        id = id,
        petId = petId,
        vetId = vetId,
        medicationName = medicationName,
        dosage = dosage,
        frequency = frequency,
        instructions = instructions,
        startDate = startDate,
        endDate = endDate,
        status = status,
        prescribedByName = prescribedByName,
        documentUrl = documentUrl,
        version = version,
    )

    private fun PrescriptionDoseEntity.toResponse() = DoseResponse(
        id = id,
        prescriptionId = prescriptionId,
        takenAt = takenAt,
        note = note,
    )

    private fun VaccineEntity.toResponse() = VaccineResponse(
        id = id,
        petId = petId,
        vaccineName = vaccineName,
        doseLabel = doseLabel,
        dueDate = dueDate,
        administeredDate = administeredDate,
        status = status,
        providerName = providerName,
        certificateUrl = certificateUrl,
        version = version,
    )
}
