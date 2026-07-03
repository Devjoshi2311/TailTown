package com.tailtown.backend.infrastructure.persistence.notifications

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notifications")
class NotificationEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "type", nullable = false)
    var type: String,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    var body: String,

    @Column(name = "deep_link")
    var deepLink: String? = null,

    @Column(name = "priority", nullable = false)
    var priority: String = "NORMAL",

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "read_at")
    var readAt: Instant? = null,

    @Column(name = "sent_at")
    var sentAt: Instant? = null,

    @Column(name = "delivery_status", nullable = false)
    var deliveryStatus: String = "CREATED",

    @Column(name = "dedupe_key")
    var dedupeKey: String? = null

) : AuditableEntity()
