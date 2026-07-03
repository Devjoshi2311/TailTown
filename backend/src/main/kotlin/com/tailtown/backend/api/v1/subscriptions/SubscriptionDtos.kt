package com.tailtown.backend.api.v1.subscriptions

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateSubscriptionRequest(
    @field:NotNull val productId: UUID?,
    @field:NotNull val addressId: UUID?,
    @field:Min(1) val quantity: Int = 1,
    @field:NotBlank val cadence: String?,
    val firstDeliveryDate: LocalDate = LocalDate.now().plusDays(1)
)

data class PauseSubscriptionRequest(
    @field:NotNull val pausedUntil: LocalDate?,
    @field:NotNull val version: Long?
)

data class ResumeSubscriptionRequest(
    @field:NotNull val version: Long?
)

data class CancelSubscriptionRequest(
    val reason: String? = null,
    @field:NotNull val version: Long?
)

data class SkipNextRequest(
    @field:NotNull val version: Long?
)

data class SubscriptionResponse(
    val id: UUID,
    val productId: UUID,
    val addressId: UUID?,
    val status: String,
    val quantity: Int,
    val cadence: String,
    val pricePerCycle: BigDecimal,
    val currency: String,
    val nextBillingDate: LocalDate?,
    val nextDeliveryDate: LocalDate,
    val pausedUntil: LocalDate?,
    val version: Long
)
