package com.tailtown.backend.infrastructure.persistence.vets

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface VetRepository : JpaRepository<VetEntity, UUID> {

    fun findAllByStatusAndDeletedAtIsNull(status: String, pageable: Pageable): Page<VetEntity>

    fun findByIdAndDeletedAtIsNull(id: UUID): VetEntity?

    fun findByCityIgnoreCaseAndStatusAndDeletedAtIsNull(
        city: String,
        status: String,
        pageable: Pageable
    ): Page<VetEntity>

    fun findAllBySpecialtyIgnoreCaseAndStatusAndDeletedAtIsNull(
        specialty: String,
        status: String,
        pageable: Pageable
    ): Page<VetEntity>
}
