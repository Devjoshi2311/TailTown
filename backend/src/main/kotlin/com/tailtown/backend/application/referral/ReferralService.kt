package com.tailtown.backend.application.referral

import com.tailtown.backend.infrastructure.persistence.referral.ReferralEntity
import com.tailtown.backend.infrastructure.persistence.referral.ReferralRepository
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.TailTownException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

class ReferralAlreadyClaimedException :
    TailTownException(ErrorCode.REFERRAL_ALREADY_CLAIMED, "Referral code has already been claimed")

class SelfReferralNotAllowedException :
    TailTownException(ErrorCode.SELF_REFERRAL_NOT_ALLOWED, "You cannot use your own referral code")

class InvalidReferralCodeException :
    TailTownException(ErrorCode.INVALID_REFERRAL_CODE, "Referral code is invalid or does not exist")

@Service
@Transactional
class ReferralService(
    private val referralRepository: ReferralRepository
) {

    companion object {
        const val REFERRER_REWARD = 100.0
        const val REFEREE_REWARD = 50.0
    }

    @Transactional(readOnly = true)
    fun getReferralSummary(userId: UUID): Map<String, Any> {
        val ownReferrals = referralRepository.findByReferrerUserIdAndDeletedAtIsNull(userId)
        val ownReferral = ownReferrals.firstOrNull()

        // Generate a code if user doesn't have one yet; creation happens lazily on first read
        val code = ownReferral?.referralCode ?: generateReferralCode(userId)

        val referralsMade = ownReferrals.count { it.status == "QUALIFIED" || it.status == "REWARDED" }.toLong()
        val rewardsEarned = ownReferrals
            .filter { it.status == "REWARDED" }
            .sumOf { it.referrerRewardAmount }

        return mapOf(
            "code" to code,
            "referralsMade" to referralsMade,
            "rewardsEarned" to rewardsEarned,
            "referrerReward" to REFERRER_REWARD,
            "refereeReward" to REFEREE_REWARD,
            "currency" to "INR"
        )
    }

    fun getOrCreateReferralCode(userId: UUID): ReferralEntity {
        val existing = referralRepository.findByReferrerUserIdAndDeletedAtIsNull(userId)
        return existing.firstOrNull() ?: referralRepository.save(
            ReferralEntity(
                referrerUserId = userId,
                referralCode = generateReferralCode(userId),
                status = "PENDING"
            )
        )
    }

    fun claimReferral(userId: UUID, code: String): ReferralEntity {
        val referral = referralRepository.findByReferralCodeAndDeletedAtIsNull(code)
            .orElseThrow { InvalidReferralCodeException() }

        // Self-referral check
        if (referral.referrerUserId == userId) throw SelfReferralNotAllowedException()

        // Check if user already claimed any referral
        val alreadyClaimed = referralRepository.findByReferredUserIdAndDeletedAtIsNull(userId)
        if (alreadyClaimed.isPresent) throw ReferralAlreadyClaimedException()

        // Check if this referral code has already been claimed by someone
        if (referral.referredUserId != null) throw ReferralAlreadyClaimedException()

        referral.referredUserId = userId
        referral.status = "QUALIFIED"
        referral.referrerRewardAmount = BigDecimal.valueOf(REFERRER_REWARD)
        referral.referredRewardAmount = BigDecimal.valueOf(REFEREE_REWARD)

        return referralRepository.save(referral)
    }

    private fun generateReferralCode(userId: UUID): String {
        val base = userId.toString().replace("-", "").uppercase()
        return "TT${base.take(8)}"
    }
}
