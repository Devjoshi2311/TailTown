package com.tailtown.backend.infrastructure.persistence.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CartItemRepository : JpaRepository<CartItemEntity, UUID> {

    fun findAllByCartIdAndDeletedAtIsNull(cartId: UUID): List<CartItemEntity>

    fun findByCartIdAndProductIdAndDeletedAtIsNull(cartId: UUID, productId: UUID): CartItemEntity?
}
