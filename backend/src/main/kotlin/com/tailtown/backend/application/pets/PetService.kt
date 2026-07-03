package com.tailtown.backend.application.pets

import com.tailtown.backend.infrastructure.persistence.pets.PetEntity
import com.tailtown.backend.infrastructure.persistence.pets.PetRepository
import com.tailtown.backend.platform.exception.ConflictException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ForbiddenException
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class PetService(
    private val petRepository: PetRepository
) {

    @Transactional(readOnly = true)
    fun getPets(userId: UUID): List<PetEntity> =
        petRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)

    @Transactional(readOnly = true)
    fun getPet(userId: UUID, petId: UUID): PetEntity {
        if (!petRepository.existsByIdAndUserIdAndDeletedAtIsNull(petId, userId)) {
            if (petRepository.existsById(petId)) {
                throw ForbiddenException("You do not have access to this pet")
            }
            throw ResourceNotFoundException("Pet", petId)
        }
        return petRepository.findByIdAndUserIdAndDeletedAtIsNull(petId, userId)
            ?: throw ResourceNotFoundException("Pet", petId)
    }

    fun createPet(userId: UUID, request: CreatePetRequest): PetEntity {
        if (request.microchipId != null &&
            petRepository.existsByMicrochipIdAndDeletedAtIsNull(request.microchipId)
        ) {
            throw ConflictException(
                ErrorCode.MICROCHIP_ALREADY_EXISTS,
                "Microchip ID '${request.microchipId}' is already registered"
            )
        }

        val entity = PetEntity(
            userId = userId,
            name = request.name,
            species = request.species,
            breed = request.breed,
            gender = request.gender,
            dateOfBirth = request.dateOfBirth,
            weightKg = request.weightKg,
            avatarUrl = request.avatarUrl,
            microchipId = request.microchipId,
            neutered = request.neutered,
            allergies = request.allergies,
            medicalNotes = request.medicalNotes
        )
        return petRepository.save(entity)
    }

    fun updatePet(userId: UUID, petId: UUID, request: UpdatePetRequest, version: Long): PetEntity {
        val pet = petRepository.findByIdAndUserIdAndDeletedAtIsNull(petId, userId)
            ?: run {
                if (petRepository.existsById(petId)) {
                    throw ForbiddenException("You do not have access to this pet")
                }
                throw ResourceNotFoundException("Pet", petId)
            }

        if (pet.version != version) {
            throw VersionConflictException()
        }

        if (request.microchipId != null &&
            request.microchipId != pet.microchipId &&
            petRepository.existsByMicrochipIdAndDeletedAtIsNull(request.microchipId)
        ) {
            throw ConflictException(
                ErrorCode.MICROCHIP_ALREADY_EXISTS,
                "Microchip ID '${request.microchipId}' is already registered"
            )
        }

        pet.name = request.name
        pet.species = request.species
        pet.breed = request.breed
        pet.gender = request.gender
        pet.dateOfBirth = request.dateOfBirth
        pet.weightKg = request.weightKg
        pet.avatarUrl = request.avatarUrl
        pet.microchipId = request.microchipId
        pet.neutered = request.neutered
        pet.allergies = request.allergies
        pet.medicalNotes = request.medicalNotes

        return petRepository.save(pet)
    }

    fun deletePet(userId: UUID, petId: UUID) {
        val pet = petRepository.findByIdAndUserIdAndDeletedAtIsNull(petId, userId)
            ?: run {
                if (petRepository.existsById(petId)) {
                    throw ForbiddenException("You do not have access to this pet")
                }
                throw ResourceNotFoundException("Pet", petId)
            }
        pet.softDelete()
        petRepository.save(pet)
    }
}
