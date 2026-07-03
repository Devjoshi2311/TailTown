package com.tailtown.backend.infrastructure.persistence.health

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface WeightRecordRepository : JpaRepository<WeightRecordEntity, UUID> {

    fun findAllByPetIdAndUserIdAndDeletedAtIsNull(petId: UUID, userId: UUID): List<WeightRecordEntity>

    fun existsByPetIdAndRecordedOnAndDeletedAtIsNull(petId: UUID, recordedOn: LocalDate): Boolean
}
