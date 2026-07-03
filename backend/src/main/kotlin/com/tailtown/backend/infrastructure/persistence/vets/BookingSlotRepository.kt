package com.tailtown.backend.infrastructure.persistence.vets

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface BookingSlotRepository : JpaRepository<BookingSlotEntity, UUID> {

    fun findAllByVetIdAndStartsAtBetweenAndStatusAndDeletedAtIsNull(
        vetId: UUID,
        from: Instant,
        to: Instant,
        status: String
    ): List<BookingSlotEntity>

    fun existsByVetIdAndStartsAtAndDeletedAtIsNull(vetId: UUID, startsAt: Instant): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM BookingSlotEntity s WHERE s.id = :id AND s.deletedAt IS NULL")
    fun findByIdForUpdate(id: UUID): BookingSlotEntity?
}
