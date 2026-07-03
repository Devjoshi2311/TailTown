package com.tailtown.backend.api.v1.referral

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class ReferralSummaryResponse(
    val code: String,
    val referrerReward: Double,
    val refereeReward: Double,
    val referralsMade: Long,
    val rewardsEarned: BigDecimal,
    val currency: String
)

data class ClaimReferralRequest(
    @field:NotBlank
    @field:Size(min = 4, max = 40)
    val referralCode: String?
)

data class ReferralResponse(
    val id: UUID,
    val referralCode: String,
    val referrerUserId: UUID,
    val referredUserId: UUID?,
    val status: String,
    val referrerRewardAmount: BigDecimal,
    val referredRewardAmount: BigDecimal,
    val currency: String
)
