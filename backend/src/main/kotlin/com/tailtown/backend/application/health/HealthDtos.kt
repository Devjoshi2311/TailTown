package com.tailtown.backend.application.health

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ── Weight ──────────────────────────────────────────────────────────────────

data class AddWeightRequest(
    @field:DecimalMin("0.1")
    @field:DecimalMax("200")
    val weightKg: BigDecimal,
    val recordedOn: LocalDate,
    val notes: String? = null,
)

data class WeightRecordResponse(
    val id: UUID,
    val petId: UUID,
    val weightKg: BigDecimal,
    val recordedOn: LocalDate,
    val source: String,
    val notes: String?,
    val version: Long,
)

// ── Prescription ─────────────────────────────────────────────────────────────

data class CreatePrescriptionRequest(
    @field:NotBlank
    val medicationName: String,
    @field:NotBlank
    val dosage: String,
    @field:NotBlank
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val instructions: String? = null,
    val documentUrl: String? = null,
)

data class PrescriptionResponse(
    val id: UUID,
    val petId: UUID,
    val vetId: UUID?,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val instructions: String?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val status: String,
    val prescribedByName: String?,
    val documentUrl: String?,
    val version: Long,
)

// ── Dose ─────────────────────────────────────────────────────────────────────

data class MarkDoseRequest(
    val takenAt: Instant? = null,
    val note: String? = null,
)

data class DoseResponse(
    val id: UUID,
    val prescriptionId: UUID,
    val takenAt: Instant,
    val note: String?,
)

// ── Vaccine ──────────────────────────────────────────────────────────────────

data class CreateVaccineRequest(
    @field:NotBlank
    val vaccineName: String,
    val doseLabel: String? = null,
    val dueDate: LocalDate? = null,
    val administeredDate: LocalDate? = null,
    val status: String? = null,
    val providerName: String? = null,
    val certificateUrl: String? = null,
    val notes: String? = null,
)

data class VaccineResponse(
    val id: UUID,
    val petId: UUID,
    val vaccineName: String,
    val doseLabel: String?,
    val dueDate: LocalDate?,
    val administeredDate: LocalDate?,
    val status: String,
    val providerName: String?,
    val certificateUrl: String?,
    val version: Long,
)
