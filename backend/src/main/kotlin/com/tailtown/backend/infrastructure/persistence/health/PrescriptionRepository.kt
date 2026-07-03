package com.tailtown.backend.infrastructure.persistence.health

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PrescriptionRepository : JpaRepository<PrescriptionEntity, UUID> {

    fun findAllByPetIdAndUserIdAndDeletedAtIsNullOrderByStartDateDesc(
        petId: UUID,
        userId: UUID
    ): List<PrescriptionEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): Optional<PrescriptionEntity>
}
