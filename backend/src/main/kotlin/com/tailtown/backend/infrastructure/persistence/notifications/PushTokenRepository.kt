package com.tailtown.backend.infrastructure.persistence.notifications

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface PushTokenRepository : JpaRepository<PushTokenEntity, UUID> {

    fun findByUserIdAndDeviceId(userId: UUID, deviceId: String): Optional<PushTokenEntity>
}
