package com.tailtown.backend.api.v1.chat

import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.UUID

data class ConversationResponse(
    val id: UUID,
    val type: String,
    val status: String,
    val subject: String?,
    val participantName: String?,
    val lastMessagePreview: String?,
    val lastMessageAt: Instant?,
    val unreadCount: Int,
    val version: Long
)

data class MessageResponse(
    val id: UUID,
    val conversationId: UUID,
    val senderType: String,
    val messageType: String,
    val body: String?,
    val attachmentUrl: String?,
    val sentAt: Instant,
    val readAt: Instant?,
    val version: Long
)

data class CreateConversationRequest(
    @field:NotBlank val type: String?,
    val vetId: UUID? = null,
    val bookingId: UUID? = null,
    val orderId: UUID? = null,
    val subject: String? = null,
    val initialMessage: String? = null
)

data class SendMessageRequest(
    @field:NotBlank val messageType: String?,
    val body: String? = null,
    val attachmentUrl: String? = null
)
