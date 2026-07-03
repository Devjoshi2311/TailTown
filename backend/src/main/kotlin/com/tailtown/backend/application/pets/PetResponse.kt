package com.tailtown.backend.application.pets

import com.tailtown.backend.infrastructure.persistence.pets.PetEntity
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.UUID

data class PetResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val species: String,
    val breed: String?,
    val gender: String?,
    val dateOfBirth: LocalDate?,
    val ageYears: Int?,
    val ageMonths: Int?,
    val weightKg: BigDecimal?,
    val avatarUrl: String?,
    val microchipId: String?,
    val neutered: Boolean?,
    val allergies: String?,
    val medicalNotes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun from(entity: PetEntity): PetResponse {
            val age = entity.dateOfBirth?.let { dob ->
                Period.between(dob, LocalDate.now())
            }
            return PetResponse(
                id = entity.id,
                userId = entity.userId,
                name = entity.name,
                species = entity.species,
                breed = entity.breed,
                gender = entity.gender,
                dateOfBirth = entity.dateOfBirth,
                ageYears = age?.years,
                ageMonths = age?.let { it.years * 12 + it.months },
                weightKg = entity.weightKg,
                avatarUrl = entity.avatarUrl,
                microchipId = entity.microchipId,
                neutered = entity.neutered,
                allergies = entity.allergies,
                medicalNotes = entity.medicalNotes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                version = entity.version
            )
        }
    }
}
