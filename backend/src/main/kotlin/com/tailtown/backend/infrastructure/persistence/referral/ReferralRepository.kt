package com.tailtown.backend.infrastructure.persistence.referral

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ReferralRepository : JpaRepository<ReferralEntity, UUID> {

    fun findByReferralCodeAndDeletedAtIsNull(referralCode: String): Optional<ReferralEntity>

    fun findByReferrerUserIdAndDeletedAtIsNull(userId: UUID): List<ReferralEntity>

    fun findByReferredUserIdAndDeletedAtIsNull(userId: UUID): Optional<ReferralEntity>

    fun countByReferrerUserIdAndStatus(userId: UUID, status: String): Long
}
