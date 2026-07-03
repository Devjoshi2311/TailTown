package com.tailtown.backend.infrastructure.persistence.orders

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderItemRepository : JpaRepository<OrderItemEntity, UUID> {

    fun findAllByOrderIdAndDeletedAtIsNull(orderId: UUID): List<OrderItemEntity>

    fun findAllByOrderIdInAndDeletedAtIsNull(orderIds: List<UUID>): List<OrderItemEntity>
}
