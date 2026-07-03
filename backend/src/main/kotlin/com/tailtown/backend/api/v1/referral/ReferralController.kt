package com.tailtown.backend.api.v1.referral

import com.tailtown.backend.application.referral.ReferralService
import com.tailtown.backend.infrastructure.persistence.referral.ReferralEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/referral")
class ReferralController(
    private val referralService: ReferralService
) {

    @GetMapping
    fun getReferralSummary(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ReferralSummaryResponse {
        val summary = referralService.getReferralSummary(principal.userId)
        return ReferralSummaryResponse(
            code = summary["code"] as String,
            referrerReward = summary["referrerReward"] as Double,
            refereeReward = summary["refereeReward"] as Double,
            referralsMade = summary["referralsMade"] as Long,
            rewardsEarned = summary["rewardsEarned"] as BigDecimal,
            currency = summary["currency"] as String
        )
    }

    @PostMapping("/claim")
    @ResponseStatus(HttpStatus.CREATED)
    fun claimReferral(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: ClaimReferralRequest
    ): ReferralResponse {
        val referral = referralService.claimReferral(principal.userId, request.referralCode!!)
        return referral.toResponse()
    }

    private fun ReferralEntity.toResponse() = ReferralResponse(
        id = id,
        referralCode = referralCode,
        referrerUserId = referrerUserId,
        referredUserId = referredUserId,
        status = status,
        referrerRewardAmount = referrerRewardAmount,
        referredRewardAmount = referredRewardAmount,
        currency = currency
    )
}
