package com.tailtown.backend.infrastructure.persistence.payments

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "payment_webhook_events")
class PaymentWebhookEventEntity(

    @Column(name = "razorpay_event_id", nullable = false, unique = true)
    var razorpayEventId: String,

    @Column(name = "event_type", nullable = false)
    var eventType: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    var payload: String? = null,

    @Column(name = "processed_at", nullable = false)
    var processedAt: Instant = Instant.now()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    var id: UUID = UUID.randomUUID()
}
