package com.tailtown.pawcare.ui.inbox

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.ui.theme.*

enum class ConversationType { VET, ORDER, SUPPORT }

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timeLabel: String,
    val unreadCount: Int = 0,
    val avatarTint: Color,
    val type: ConversationType,
)

data class FileAttachment(val name: String, val pages: Int, val sizeKb: Int)

data class ChatMessage(
    val id: String,
    val text: String? = null,
    val attachment: FileAttachment? = null,
    val isFromMe: Boolean,
    val timeLabel: String,
    val isRead: Boolean = false,
)

enum class NotificationType { APPOINTMENT, MEDICATION, DELIVERY, OFFER }

data class AppNotification(
    val id: String,
    val title: String,
    val subtitle: String,
    val timeLabel: String,
    val type: NotificationType,
    val isUnread: Boolean = false,
)

val sampleConversations = listOf(
    Conversation(
        id = "dr-anjali",
        name = "Dr. Anjali Mehta",
        lastMessage = "Bruno's reports look great. Continue...",
        timeLabel = "2m",
        unreadCount = 2,
        avatarTint = CoralSoft,
        type = ConversationType.VET,
    ),
    Conversation(
        id = "order-vet2406",
        name = "Order #VET2406",
        lastMessage = "Your order is packed and ready",
        timeLabel = "1h",
        unreadCount = 0,
        avatarTint = Color(0xFFDDE5F4),
        type = ConversationType.ORDER,
    ),
    Conversation(
        id = "pawcare-support",
        name = "Pawcare support",
        lastMessage = "Thanks for reaching out. How can w...",
        timeLabel = "Yest",
        unreadCount = 0,
        avatarTint = Bone,
        type = ConversationType.SUPPORT,
    ),
)

val sampleMessages = listOf(
    ChatMessage(
        id = "m1",
        text = "Bruno's reports look great. Continue the meds for 9 more days.",
        isFromMe = false,
        timeLabel = "11:32",
        isRead = true,
    ),
    ChatMessage(
        id = "m2",
        attachment = FileAttachment("Lab report.pdf", 2, 340),
        isFromMe = false,
        timeLabel = "11:33",
        isRead = true,
    ),
    ChatMessage(
        id = "m3",
        text = "Got it! Thanks doctor 🐾",
        isFromMe = true,
        timeLabel = "11:34",
        isRead = true,
    ),
)

val sampleNotifications = listOf(
    AppNotification(
        id = "n1",
        title = "Appointment in 2 hours",
        subtitle = "Dr. Mehta at 11:30 AM · Indirapuram",
        timeLabel = "2m ago",
        type = NotificationType.APPOINTMENT,
        isUnread = true,
    ),
    AppNotification(
        id = "n2",
        title = "Time for Bruno's meds",
        subtitle = "Apoquel · 8 AM dose",
        timeLabel = "1h ago",
        type = NotificationType.MEDICATION,
        isUnread = true,
    ),
    AppNotification(
        id = "n3",
        title = "Out for delivery",
        subtitle = "Order #VET2406 · Arriving today",
        timeLabel = "3h ago",
        type = NotificationType.DELIVERY,
        isUnread = false,
    ),
    AppNotification(
        id = "n4",
        title = "20% off premium food",
        subtitle = "Ends tonight · Use code SAVE20",
        timeLabel = "1 day",
        type = NotificationType.OFFER,
        isUnread = false,
    ),
)

data class AccountUser(
    val name: String,
    val phone: String,
    val initials: String,
    val petCount: Int,
    val activeSubscriptions: Int,
)

val sampleAccountUser = AccountUser(
    name = "Riya Sharma",
    phone = "+91 98765 43210",
    initials = "R",
    petCount = 2,
    activeSubscriptions = 2,
)
