package com.tailtown.backend.infrastructure.persistence.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CartRepository : JpaRepository<CartEntity, UUID> {

    fun findByUserIdAndStatusAndDeletedAtIsNull(userId: UUID, status: String): CartEntity?
}
