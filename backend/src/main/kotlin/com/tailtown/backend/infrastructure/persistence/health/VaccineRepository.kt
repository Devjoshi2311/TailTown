package com.tailtown.backend.infrastructure.persistence.health

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface VaccineRepository : JpaRepository<VaccineEntity, UUID> {

    fun findAllByPetIdAndUserIdAndDeletedAtIsNull(petId: UUID, userId: UUID): List<VaccineEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): Optional<VaccineEntity>
}
