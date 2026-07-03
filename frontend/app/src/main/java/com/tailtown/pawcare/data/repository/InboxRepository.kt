package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.inbox.ChatMessage
import com.tailtown.pawcare.ui.inbox.Conversation

interface InboxRepository {
    suspend fun getConversations(): List<Conversation>
    suspend fun getMessages(conversationId: String): List<ChatMessage>
    suspend fun sendMessage(conversationId: String, text: String): ChatMessage
}
