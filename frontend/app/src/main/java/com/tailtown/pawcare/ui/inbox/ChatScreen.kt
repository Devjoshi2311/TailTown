package com.tailtown.pawcare.ui.inbox

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun ChatScreen(
    conversationId: String,
    contactName: String = "",
    messages: List<ChatMessage> = sampleMessages,
    onSendMessage: (String) -> Unit = {},
    onBack: () -> Unit,
) {
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .imePadding(),
    ) {
        ChatTopBar(contactName = contactName.ifBlank { "Support" }, onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        ) {
            // Date header
            item {
                val dateStr = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM"))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Messages
            items(messages) { message ->
                ChatBubble(message = message)
                Spacer(Modifier.height(8.dp))
            }
        }

        ChatInputBar(
            value = messageText,
            onValueChange = { messageText = it },
            onSend = {
                if (messageText.isNotBlank()) {
                    onSendMessage(messageText)
                    messageText = ""
                }
            },
        )
    }
}

@Composable
private fun ChatTopBar(contactName: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink900,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = contactName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Teal600),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = Teal600,
                    )
                }
            }
            // Spacer to balance the back button and keep column centered
            Spacer(Modifier.size(48.dp))
        }
        HorizontalDivider(color = Hairline)
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Hairline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Message...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Bone,
                    unfocusedContainerColor = Bone,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink900),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Coral),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onSend) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    if (message.attachment != null) {
        AttachmentBubble(
            attachment = message.attachment,
            isFromMe = message.isFromMe,
            timeLabel = message.timeLabel,
        )
        return
    }

    val text = message.text ?: return

    if (message.isFromMe) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 4.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp,
                        ),
                    )
                    .background(Coral)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = White,
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${message.timeLabel} ✓✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = White.copy(alpha = 0.75f),
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 60.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp,
                        ),
                    )
                    .background(White)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = message.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )
        }
    }
}

@Composable
private fun AttachmentBubble(
    attachment: FileAttachment,
    isFromMe: Boolean,
    timeLabel: String,
) {
    val alignment = if (isFromMe) Alignment.End else Alignment.Start
    val startPad = if (isFromMe) 60.dp else 0.dp
    val endPad = if (isFromMe) 0.dp else 60.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = startPad, end = endPad),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(12.dp))
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // PDF icon box
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CoralSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "PDF",
                        style = MaterialTheme.typography.labelSmall,
                        color = Coral,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = attachment.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Ink900,
                    )
                    Text(
                        text = "${attachment.pages} pages · ${attachment.sizeKb} KB",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = timeLabel,
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    PawcareTheme {
        ChatScreen(
            conversationId = "dr-anjali",
            onBack = {},
        )
    }
}
