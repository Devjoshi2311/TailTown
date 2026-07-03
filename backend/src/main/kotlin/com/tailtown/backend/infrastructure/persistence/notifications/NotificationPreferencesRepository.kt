package com.tailtown.backend.infrastructure.persistence.notifications

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface NotificationPreferencesRepository : JpaRepository<NotificationPreferencesEntity, UUID> {

    fun findByUserId(userId: UUID): Optional<NotificationPreferencesEntity>
}
