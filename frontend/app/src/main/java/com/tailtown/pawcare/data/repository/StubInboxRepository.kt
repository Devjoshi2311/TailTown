package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.inbox.ChatMessage
import com.tailtown.pawcare.ui.inbox.sampleConversations
import com.tailtown.pawcare.ui.inbox.sampleMessages

class StubInboxRepository : InboxRepository {
    override suspend fun getConversations() = sampleConversations
    override suspend fun getMessages(conversationId: String) = sampleMessages
    override suspend fun sendMessage(conversationId: String, text: String) = ChatMessage(
        id = "m${System.currentTimeMillis()}",
        text = text,
        isFromMe = true,
        timeLabel = "Now",
        isRead = false,
    )
}
