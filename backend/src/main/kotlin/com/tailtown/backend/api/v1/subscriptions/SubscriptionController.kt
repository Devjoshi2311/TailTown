package com.tailtown.backend.api.v1.subscriptions

import com.tailtown.backend.application.subscriptions.SubscriptionService
import com.tailtown.backend.infrastructure.persistence.subscriptions.SubscriptionEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    @GetMapping
    fun getSubscriptions(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "") status: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<SubscriptionResponse> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val result = subscriptionService.getSubscriptions(principal.userId, status, pageable)
        return result.content.map { it.toResponse() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSubscription(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateSubscriptionRequest
    ): SubscriptionResponse {
        val entity = subscriptionService.createSubscription(
            userId = principal.userId,
            productId = request.productId!!,
            addressId = request.addressId,
            quantity = request.quantity,
            cadence = request.cadence!!,
            firstDeliveryDate = request.firstDeliveryDate
        )
        return entity.toResponse()
    }

    @PatchMapping("/{id}/pause")
    fun pauseSubscription(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: PauseSubscriptionRequest
    ): SubscriptionResponse {
        val entity = subscriptionService.pauseSubscription(
            userId = principal.userId,
            id = id,
            pausedUntil = request.pausedUntil!!,
            version = request.version!!
        )
        return entity.toResponse()
    }

    @PatchMapping("/{id}/resume")
    fun resumeSubscription(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ResumeSubscriptionRequest
    ): SubscriptionResponse {
        val entity = subscriptionService.resumeSubscription(
            userId = principal.userId,
            id = id,
            version = request.version!!
        )
        return entity.toResponse()
    }

    @PatchMapping("/{id}/skip-next")
    fun skipNextDelivery(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: SkipNextRequest
    ): SubscriptionResponse {
        val entity = subscriptionService.skipNextDelivery(
            userId = principal.userId,
            id = id,
            version = request.version!!
        )
        return entity.toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelSubscription(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody(required = false) request: CancelSubscriptionRequest?
    ) {
        subscriptionService.cancelSubscription(
            userId = principal.userId,
            id = id,
            reason = request?.reason,
            version = request?.version ?: 0L
        )
    }

    private fun SubscriptionEntity.toResponse() = SubscriptionResponse(
        id = id,
        productId = productId,
        addressId = addressId,
        status = status,
        quantity = quantity,
        cadence = cadence,
        pricePerCycle = pricePerCycle,
        currency = currency,
        nextBillingDate = nextBillingDate,
        nextDeliveryDate = nextDeliveryDate,
        pausedUntil = pausedUntil,
        version = version
    )
}
