package com.tailtown.backend.infrastructure.persistence.pets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PetRepository : JpaRepository<PetEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId: UUID): List<PetEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): PetEntity?

    fun existsByMicrochipIdAndDeletedAtIsNull(microchipId: String): Boolean

    fun existsByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): Boolean
}
