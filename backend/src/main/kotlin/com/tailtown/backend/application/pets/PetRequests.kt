package com.tailtown.backend.application.pets

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class CreatePetRequest(

    @field:NotBlank(message = "Pet name is required")
    @field:Size(max = 120, message = "Name must not exceed 120 characters")
    val name: String,

    @field:NotBlank(message = "Species is required")
    @field:Size(max = 40, message = "Species must not exceed 40 characters")
    val species: String,

    @field:Size(max = 120, message = "Breed must not exceed 120 characters")
    val breed: String? = null,

    @field:Size(max = 24, message = "Gender must not exceed 24 characters")
    val gender: String? = null,

    val dateOfBirth: LocalDate? = null,

    @field:DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    val weightKg: BigDecimal? = null,

    val avatarUrl: String? = null,

    @field:Size(max = 80, message = "Microchip ID must not exceed 80 characters")
    val microchipId: String? = null,

    val neutered: Boolean? = null,

    val allergies: String? = null,

    val medicalNotes: String? = null
)

data class UpdatePetRequest(

    @field:NotBlank(message = "Pet name is required")
    @field:Size(max = 120, message = "Name must not exceed 120 characters")
    val name: String,

    @field:NotBlank(message = "Species is required")
    @field:Size(max = 40, message = "Species must not exceed 40 characters")
    val species: String,

    @field:Size(max = 120, message = "Breed must not exceed 120 characters")
    val breed: String? = null,

    @field:Size(max = 24, message = "Gender must not exceed 24 characters")
    val gender: String? = null,

    val dateOfBirth: LocalDate? = null,

    @field:DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    val weightKg: BigDecimal? = null,

    val avatarUrl: String? = null,

    @field:Size(max = 80, message = "Microchip ID must not exceed 80 characters")
    val microchipId: String? = null,

    val neutered: Boolean? = null,

    val allergies: String? = null,

    val medicalNotes: String? = null,

    @field:NotNull(message = "Version is required for updates")
    val version: Long
)
