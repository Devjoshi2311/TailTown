package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ConversationTypeDto {
    @SerialName("vet") VET,
    @SerialName("order") ORDER,
    @SerialName("support") SUPPORT,
}

@Serializable
data class ConversationDto(
    val id: String,
    val name: String,
    @SerialName("last_message") val lastMessage: String,
    @SerialName("time_label") val timeLabel: String,
    @SerialName("unread_count") val unreadCount: Int = 0,
    val type: ConversationTypeDto,
)

@Serializable
data class InboxData(val conversations: List<ConversationDto>)

@Serializable
data class FileAttachmentDto(val name: String, val pages: Int, @SerialName("size_kb") val sizeKb: Int)

@Serializable
data class ChatMessageDto(
    val id: String,
    val text: String? = null,
    val attachment: FileAttachmentDto? = null,
    @SerialName("is_from_me") val isFromMe: Boolean,
    @SerialName("time_label") val timeLabel: String,
    @SerialName("is_read") val isRead: Boolean = false,
)

@Serializable
data class ChatData(val messages: List<ChatMessageDto>)

@Serializable
data class SendMessageRequest(
    @SerialName("conversation_id") val conversationId: String,
    val text: String,
)

@Serializable
enum class NotificationTypeDto {
    @SerialName("appointment") APPOINTMENT,
    @SerialName("medication") MEDICATION,
    @SerialName("delivery") DELIVERY,
    @SerialName("offer") OFFER,
}

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val subtitle: String,
    @SerialName("time_label") val timeLabel: String,
    val type: NotificationTypeDto,
    @SerialName("is_unread") val isUnread: Boolean = false,
)

@Serializable
data class NotificationsData(val notifications: List<NotificationDto>)
