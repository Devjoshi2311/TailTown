package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.TokenStore
import com.tailtown.pawcare.data.remote.dto.SendMessageRequestDto
import com.tailtown.pawcare.data.repository.InboxRepository
import com.tailtown.pawcare.ui.inbox.ChatMessage
import com.tailtown.pawcare.ui.inbox.Conversation
import com.tailtown.pawcare.ui.inbox.ConversationType
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Teal600
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteInboxRepository @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : InboxRepository {

    override suspend fun getConversations(): List<Conversation> {
        val list = api.getConversations().data ?: return emptyList()
        return list.mapIndexed { index, dto ->
            Conversation(
                id = dto.id,
                name = dto.participantName,
                lastMessage = dto.lastMessage,
                timeLabel = formatTime(dto.updatedAt),
                unreadCount = dto.unreadCount,
                avatarTint = if (index % 2 == 0) Teal600 else Coral,
                type = ConversationType.SUPPORT,
            )
        }
    }

    override suspend fun getMessages(conversationId: String): List<ChatMessage> {
        val myId = tokenStore.getUserId()
        return api.getMessages(conversationId).data?.map { dto ->
            ChatMessage(
                id = dto.id,
                text = dto.text,
                isFromMe = dto.senderId == myId,
                timeLabel = formatTime(dto.createdAt),
                isRead = dto.isRead,
            )
        } ?: emptyList()
    }

    override suspend fun sendMessage(conversationId: String, text: String): ChatMessage {
        val dto = api.sendMessage(conversationId, SendMessageRequestDto(text)).data!!
        return ChatMessage(
            id = dto.id,
            text = dto.text,
            isFromMe = true,
            timeLabel = formatTime(dto.createdAt),
            isRead = false,
        )
    }

    // "2026-06-11T10:30:00.000Z" → "10:30"
    private fun formatTime(iso: String): String =
        try { iso.substring(11, 16) } catch (_: Exception) { "Now" }
}
