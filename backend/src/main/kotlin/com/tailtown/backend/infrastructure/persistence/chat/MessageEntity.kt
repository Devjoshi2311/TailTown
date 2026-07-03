package com.tailtown.backend.infrastructure.persistence.chat

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "messages")
class MessageEntity(

    @Column(name = "conversation_id", nullable = false, columnDefinition = "uuid")
    var conversationId: UUID,

    @Column(name = "sender_user_id", columnDefinition = "uuid")
    var senderUserId: UUID? = null,

    @Column(name = "sender_vet_id", columnDefinition = "uuid")
    var senderVetId: UUID? = null,

    @Column(name = "sender_type", nullable = false)
    var senderType: String,

    @Column(name = "message_type", nullable = false)
    var messageType: String = "TEXT",

    @Column(name = "body", columnDefinition = "TEXT")
    var body: String? = null,

    @Column(name = "attachment_url")
    var attachmentUrl: String? = null,

    @Column(name = "sent_at", nullable = false)
    var sentAt: Instant = Instant.now(),

    @Column(name = "read_at")
    var readAt: Instant? = null

) : AuditableEntity()
