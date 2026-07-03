package com.tailtown.backend.infrastructure.persistence.orders

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNull(userId: UUID, pageable: Pageable): Page<OrderEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): OrderEntity?
}
