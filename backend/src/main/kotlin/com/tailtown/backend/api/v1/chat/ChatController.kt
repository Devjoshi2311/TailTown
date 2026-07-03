package com.tailtown.backend.api.v1.chat

import com.tailtown.backend.application.chat.ChatService
import com.tailtown.backend.infrastructure.persistence.chat.ConversationEntity
import com.tailtown.backend.infrastructure.persistence.chat.MessageEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class ChatController(
    private val chatService: ChatService
) {

    @GetMapping("/conversations")
    fun getConversations(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<ConversationResponse> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"))
        val result = chatService.getConversations(principal.userId, pageable)
        return result.content.map { it.toResponse() }
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    fun createConversation(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateConversationRequest
    ): ConversationResponse {
        val conversation = chatService.createConversation(
            userId = principal.userId,
            type = request.type!!,
            vetId = request.vetId,
            bookingId = request.bookingId,
            orderId = request.orderId,
            subject = request.subject,
            initialMessage = request.initialMessage
        )
        return conversation.toResponse()
    }

    @GetMapping("/conversations/{id}/messages")
    fun getMessages(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "30") size: Int
    ): List<MessageResponse> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
        val result = chatService.getMessages(principal.userId, id, pageable)
        return result.content.map { it.toResponse() }
    }

    @PostMapping("/conversations/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun sendMessage(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: SendMessageRequest
    ): MessageResponse {
        val message = chatService.sendMessage(
            userId = principal.userId,
            conversationId = id,
            messageType = request.messageType!!,
            body = request.body,
            attachmentUrl = request.attachmentUrl
        )
        return message.toResponse()
    }

    @PatchMapping("/conversations/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markConversationRead(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ) {
        chatService.markConversationRead(principal.userId, id)
    }

    private fun ConversationEntity.toResponse() = ConversationResponse(
        id = id,
        type = type,
        status = status,
        subject = subject,
        participantName = null,
        lastMessagePreview = lastMessagePreview,
        lastMessageAt = lastMessageAt,
        unreadCount = unreadCountUser,
        version = version
    )

    private fun MessageEntity.toResponse() = MessageResponse(
        id = id,
        conversationId = conversationId,
        senderType = senderType,
        messageType = messageType,
        body = body,
        attachmentUrl = attachmentUrl,
        sentAt = sentAt,
        readAt = readAt,
        version = version
    )
}
