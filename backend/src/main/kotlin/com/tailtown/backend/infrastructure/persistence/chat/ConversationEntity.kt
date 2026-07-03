package com.tailtown.backend.infrastructure.persistence.chat

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "conversations")
class ConversationEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "vet_id", columnDefinition = "uuid")
    var vetId: UUID? = null,

    @Column(name = "booking_id", columnDefinition = "uuid")
    var bookingId: UUID? = null,

    @Column(name = "order_id", columnDefinition = "uuid")
    var orderId: UUID? = null,

    @Column(name = "type", nullable = false)
    var type: String = "SUPPORT",

    @Column(name = "status", nullable = false)
    var status: String = "OPEN",

    @Column(name = "subject")
    var subject: String? = null,

    @Column(name = "last_message_preview")
    var lastMessagePreview: String? = null,

    @Column(name = "last_message_at")
    var lastMessageAt: Instant? = null,

    @Column(name = "unread_count_user", nullable = false)
    var unreadCountUser: Int = 0,

    @Column(name = "unread_count_admin", nullable = false)
    var unreadCountAdmin: Int = 0

) : AuditableEntity()
