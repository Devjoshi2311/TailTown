package com.tailtown.backend.application.subscriptions

import com.tailtown.backend.infrastructure.persistence.subscriptions.SubscriptionEntity
import com.tailtown.backend.infrastructure.persistence.subscriptions.SubscriptionRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.ValidationException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository
) {

    @Transactional(readOnly = true)
    fun getSubscriptions(userId: UUID, status: String?, pageable: Pageable): Page<SubscriptionEntity> {
        return subscriptionRepository.findAllByUserIdAndStatusNotAndDeletedAtIsNull(
            userId = userId,
            excludeStatus = "DELETED",
            pageable = pageable
        )
    }

    fun createSubscription(
        userId: UUID,
        productId: UUID,
        addressId: UUID?,
        quantity: Int,
        cadence: String,
        firstDeliveryDate: LocalDate
    ): SubscriptionEntity {
        val entity = SubscriptionEntity(
            userId = userId,
            productId = productId,
            addressId = addressId,
            quantity = quantity,
            cadence = cadence,
            pricePerCycle = BigDecimal.ZERO,
            nextDeliveryDate = firstDeliveryDate,
            nextBillingDate = firstDeliveryDate,
            status = "ACTIVE"
        )
        return subscriptionRepository.save(entity)
    }

    fun pauseSubscription(
        userId: UUID,
        id: UUID,
        pausedUntil: LocalDate,
        version: Long
    ): SubscriptionEntity {
        val entity = subscriptionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
            .orElseThrow { ResourceNotFoundException("Subscription", id) }

        if (entity.version != version) throw VersionConflictException()
        if (entity.status != "ACTIVE") throw ValidationException("Only active subscriptions can be paused")

        entity.status = "PAUSED"
        entity.pausedUntil = pausedUntil
        return subscriptionRepository.save(entity)
    }

    fun resumeSubscription(
        userId: UUID,
        id: UUID,
        version: Long
    ): SubscriptionEntity {
        val entity = subscriptionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
            .orElseThrow { ResourceNotFoundException("Subscription", id) }

        if (entity.version != version) throw VersionConflictException()
        if (entity.status != "PAUSED") throw ValidationException("Only paused subscriptions can be resumed")

        entity.status = "ACTIVE"
        entity.pausedUntil = null
        return subscriptionRepository.save(entity)
    }

    fun cancelSubscription(
        userId: UUID,
        id: UUID,
        reason: String?,
        version: Long
    ): SubscriptionEntity {
        val entity = subscriptionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
            .orElseThrow { ResourceNotFoundException("Subscription", id) }

        if (entity.version != version) throw VersionConflictException()
        if (entity.status == "CANCELLED") throw ValidationException("Subscription is already cancelled")

        entity.status = "CANCELLED"
        entity.cancelledAt = Instant.now()
        entity.cancellationReason = reason
        return subscriptionRepository.save(entity)
    }

    fun skipNextDelivery(
        userId: UUID,
        id: UUID,
        version: Long
    ): SubscriptionEntity {
        val entity = subscriptionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
            .orElseThrow { ResourceNotFoundException("Subscription", id) }

        if (entity.version != version) throw VersionConflictException()
        if (entity.status != "ACTIVE") throw ValidationException("Only active subscriptions can have deliveries skipped")

        // Advance next delivery date by one cadence cycle
        val nextDate = when (entity.cadence.uppercase()) {
            "WEEKLY" -> entity.nextDeliveryDate.plusWeeks(1)
            "BIWEEKLY" -> entity.nextDeliveryDate.plusWeeks(2)
            "MONTHLY" -> entity.nextDeliveryDate.plusMonths(1)
            else -> entity.nextDeliveryDate.plusMonths(1)
        }
        entity.nextDeliveryDate = nextDate
        entity.nextBillingDate = nextDate
        return subscriptionRepository.save(entity)
    }
}
