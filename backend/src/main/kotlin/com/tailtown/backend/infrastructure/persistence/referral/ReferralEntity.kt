package com.tailtown.backend.infrastructure.persistence.referral

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "referrals")
class ReferralEntity(

    @Column(name = "referrer_user_id", nullable = false, columnDefinition = "uuid")
    var referrerUserId: UUID,

    @Column(name = "referred_user_id", columnDefinition = "uuid")
    var referredUserId: UUID? = null,

    @Column(name = "referral_code", nullable = false, unique = true)
    var referralCode: String,

    @Column(name = "status", nullable = false)
    var status: String = "PENDING",

    @Column(name = "referrer_reward_amount", nullable = false, precision = 12, scale = 2)
    var referrerRewardAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "referred_reward_amount", nullable = false, precision = 12, scale = 2)
    var referredRewardAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "qualified_at")
    var qualifiedAt: Instant? = null,

    @Column(name = "rewarded_at")
    var rewardedAt: Instant? = null,

    @Column(name = "fraud_reason")
    var fraudReason: String? = null

) : AuditableEntity()
