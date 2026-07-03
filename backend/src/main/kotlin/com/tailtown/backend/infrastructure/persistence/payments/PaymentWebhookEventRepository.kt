package com.tailtown.backend.infrastructure.persistence.payments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentWebhookEventRepository : JpaRepository<PaymentWebhookEventEntity, UUID> {

    fun existsByRazorpayEventId(razorpayEventId: String): Boolean
}
