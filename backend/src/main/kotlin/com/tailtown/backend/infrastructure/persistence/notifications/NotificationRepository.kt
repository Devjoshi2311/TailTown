package com.tailtown.backend.infrastructure.persistence.notifications

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<NotificationEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        userId: UUID,
        pageable: Pageable
    ): Page<NotificationEntity>

    fun findAllByUserIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(
        userId: UUID,
        pageable: Pageable
    ): Page<NotificationEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(
        id: UUID,
        userId: UUID
    ): Optional<NotificationEntity>
}
