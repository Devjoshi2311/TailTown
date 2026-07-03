package com.tailtown.backend.infrastructure.persistence.booking

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface BookingRepository : JpaRepository<BookingEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNull(userId: UUID, pageable: Pageable): Page<BookingEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(id: UUID, userId: UUID): BookingEntity?

    fun findByRazorpayOrderIdAndDeletedAtIsNull(razorpayOrderId: String): BookingEntity?

    fun findAllByStatusAndRazorpayOrderIdIsNotNullAndCreatedAtBeforeAndDeletedAtIsNull(
        status: String,
        before: Instant
    ): List<BookingEntity>
}
