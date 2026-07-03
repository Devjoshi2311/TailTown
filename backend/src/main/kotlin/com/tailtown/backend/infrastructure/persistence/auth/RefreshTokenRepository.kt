package com.tailtown.backend.infrastructure.persistence.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {

    fun findByTokenAndRevokedAtIsNullAndExpiresAtAfter(token: String, now: Instant): RefreshTokenEntity?

    fun findAllByUserIdAndRevokedAtIsNull(userId: UUID): List<RefreshTokenEntity>

    fun deleteAllByExpiresAtBefore(cutoff: Instant)
}
