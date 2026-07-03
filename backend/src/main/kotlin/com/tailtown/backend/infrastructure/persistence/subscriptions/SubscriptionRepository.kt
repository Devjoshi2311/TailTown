package com.tailtown.backend.infrastructure.persistence.subscriptions

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@Repository
interface SubscriptionRepository : JpaRepository<SubscriptionEntity, UUID> {

    fun findAllByUserIdAndStatusNotAndDeletedAtIsNull(
        userId: UUID,
        excludeStatus: String,
        pageable: Pageable
    ): Page<SubscriptionEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(
        id: UUID,
        userId: UUID
    ): Optional<SubscriptionEntity>

    fun findAllByNextBillingDateAndStatus(
        date: LocalDate,
        status: String
    ): List<SubscriptionEntity>
}
