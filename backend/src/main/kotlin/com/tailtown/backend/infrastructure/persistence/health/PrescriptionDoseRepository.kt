package com.tailtown.backend.infrastructure.persistence.health

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PrescriptionDoseRepository : JpaRepository<PrescriptionDoseEntity, UUID> {

    fun findAllByPrescriptionIdOrderByTakenAtDesc(prescriptionId: UUID): List<PrescriptionDoseEntity>
}
