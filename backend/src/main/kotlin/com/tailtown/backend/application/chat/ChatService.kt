package com.tailtown.backend.application.chat

import com.tailtown.backend.infrastructure.persistence.chat.ConversationEntity
import com.tailtown.backend.infrastructure.persistence.chat.ConversationRepository
import com.tailtown.backend.infrastructure.persistence.chat.MessageEntity
import com.tailtown.backend.infrastructure.persistence.chat.MessageRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.ValidationException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.TailTownException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

class ConversationClosedException :
    TailTownException(ErrorCode.CONVERSATION_CLOSED, "Conversation is closed and cannot receive new messages")

@Service
@Transactional
class ChatService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) {

    @Transactional(readOnly = true)
    fun getConversations(userId: UUID, pageable: Pageable): Page<ConversationEntity> {
        return conversationRepository.findAllByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc(userId, pageable)
    }

    fun createConversation(
        userId: UUID,
        type: String,
        vetId: UUID?,
        bookingId: UUID?,
        orderId: UUID?,
        subject: String?,
        initialMessage: String?
    ): ConversationEntity {
        val conversation = ConversationEntity(
            userId = userId,
            type = type,
            vetId = vetId,
            bookingId = bookingId,
            orderId = orderId,
            subject = subject,
            status = "OPEN"
        )

        val savedConversation = conversationRepository.save(conversation)

        if (!initialMessage.isNullOrBlank()) {
            val message = MessageEntity(
                conversationId = savedConversation.id,
                senderUserId = userId,
                senderType = "USER",
                messageType = "TEXT",
                body = initialMessage,
                sentAt = Instant.now()
            )
            messageRepository.save(message)

            savedConversation.lastMessagePreview = initialMessage.take(100)
            savedConversation.lastMessageAt = message.sentAt
            savedConversation.unreadCountAdmin = 1
            conversationRepository.save(savedConversation)
        }

        return savedConversation
    }

    @Transactional(readOnly = true)
    fun getMessages(userId: UUID, conversationId: UUID, pageable: Pageable): Page<MessageEntity> {
        conversationRepository.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
            .orElseThrow { ResourceNotFoundException("Conversation", conversationId) }

        return messageRepository.findAllByConversationIdAndDeletedAtIsNullOrderBySentAtDesc(conversationId, pageable)
    }

    fun sendMessage(
        userId: UUID,
        conversationId: UUID,
        messageType: String,
        body: String?,
        attachmentUrl: String?
    ): MessageEntity {
        val conversation = conversationRepository.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
            .orElseThrow { ResourceNotFoundException("Conversation", conversationId) }

        if (conversation.status != "OPEN") throw ConversationClosedException()

        val message = MessageEntity(
            conversationId = conversationId,
            senderUserId = userId,
            senderType = "USER",
            messageType = messageType,
            body = body,
            attachmentUrl = attachmentUrl,
            sentAt = Instant.now()
        )
        val savedMessage = messageRepository.save(message)

        // Update conversation with last message preview
        conversation.lastMessagePreview = body?.take(100)
        conversation.lastMessageAt = savedMessage.sentAt
        conversation.unreadCountAdmin = conversation.unreadCountAdmin + 1
        conversationRepository.save(conversation)

        return savedMessage
    }

    fun markConversationRead(userId: UUID, conversationId: UUID) {
        val conversation = conversationRepository.findByIdAndUserIdAndDeletedAtIsNull(conversationId, userId)
            .orElseThrow { ResourceNotFoundException("Conversation", conversationId) }

        conversation.unreadCountUser = 0
        conversationRepository.save(conversation)
    }
}
