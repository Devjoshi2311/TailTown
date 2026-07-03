package com.tailtown.backend.infrastructure.persistence.profile

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AddressRepository : JpaRepository<AddressEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNull(userId: UUID): List<AddressEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): AddressEntity?

    fun findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userId: UUID): AddressEntity?

    fun existsByUserIdAndLabelAndDeletedAtIsNull(userId: UUID, label: String): Boolean
}
