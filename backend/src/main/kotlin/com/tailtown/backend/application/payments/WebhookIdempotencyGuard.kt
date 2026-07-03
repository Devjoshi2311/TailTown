package com.tailtown.backend.application.payments

import com.tailtown.backend.infrastructure.persistence.payments.PaymentWebhookEventEntity
import com.tailtown.backend.infrastructure.persistence.payments.PaymentWebhookEventRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * A separate bean (not just a method on PaymentService) so this runs in its own REQUIRES_NEW
 * transaction via Spring's proxy — self-invocation from within PaymentService would silently
 * skip the proxy and lose that guarantee, letting two concurrent webhook deliveries both proceed.
 */
@Component
class WebhookIdempotencyGuard(
    private val webhookEventRepository: PaymentWebhookEventRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun recordIfNew(eventId: String, eventType: String, rawBody: String): Boolean {
        if (webhookEventRepository.existsByRazorpayEventId(eventId)) return false
        return try {
            webhookEventRepository.saveAndFlush(
                PaymentWebhookEventEntity(razorpayEventId = eventId, eventType = eventType, payload = rawBody)
            )
            true
        } catch (e: DataIntegrityViolationException) {
            false
        }
    }
}
