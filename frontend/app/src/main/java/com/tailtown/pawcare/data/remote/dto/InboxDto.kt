package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponseDto(
    val id: String,
    val participantName: String,
    val participantAvatarUrl: String? = null,
    val lastMessage: String,
    val unreadCount: Int,
    val updatedAt: String,
)

@Serializable
data class MessageResponseDto(
    val id: String,
    val senderId: String,
    val text: String,
    val isRead: Boolean,
    val createdAt: String,
)

@Serializable
data class SendMessageRequestDto(val text: String)
