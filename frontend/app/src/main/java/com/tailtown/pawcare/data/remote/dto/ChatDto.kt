package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequestDto(
    @SerialName("type") val type: String,
    @SerialName("vetId") val vetId: String? = null,
    @SerialName("bookingId") val bookingId: String? = null,
    @SerialName("orderId") val orderId: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("initialMessage") val initialMessage: String? = null,
)
