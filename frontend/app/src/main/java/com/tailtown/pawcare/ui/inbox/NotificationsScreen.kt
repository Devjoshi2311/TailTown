package com.tailtown.pawcare.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.BoneWarm
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.White

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onAppointmentTap: () -> Unit = {},
    onMedicationTap: () -> Unit = {},
    onDeliveryTap: () -> Unit = {},
    onPromoTap: () -> Unit = {},
) {
    val todayNotifications = sampleNotifications.take(3)
    val yesterdayNotification = sampleNotifications[3]

    Scaffold(containerColor = Bone) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
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
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink900,
                )
            }

            // ── Content ───────────────────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                // TODAY section
                item {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White),
                    ) {
                        todayNotifications.forEachIndexed { index, notification ->
                            NotificationRow(
                                notification = notification,
                                onClick = when (notification.type) {
                                    NotificationType.APPOINTMENT -> onAppointmentTap
                                    NotificationType.MEDICATION -> onMedicationTap
                                    NotificationType.DELIVERY -> onDeliveryTap
                                    NotificationType.OFFER -> onPromoTap
                                },
                            )
                            if (index < todayNotifications.lastIndex) {
                                HorizontalDivider(color = Hairline, thickness = 1.dp)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }

                // YESTERDAY section
                item {
                    Text(
                        text = "YESTERDAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White),
                    ) {
                        NotificationRow(
                            notification = yesterdayNotification,
                            onClick = when (yesterdayNotification.type) {
                                NotificationType.APPOINTMENT -> onAppointmentTap
                                NotificationType.MEDICATION -> onMedicationTap
                                NotificationType.DELIVERY -> onDeliveryTap
                                NotificationType.OFFER -> onPromoTap
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: AppNotification, onClick: () -> Unit = {}) {
    val avatarBg = when (notification.type) {
        NotificationType.APPOINTMENT -> CoralSoft
        NotificationType.MEDICATION -> Color(0xFFEEEBFC)
        NotificationType.DELIVERY -> Color(0xFFD6EFE8)
        NotificationType.OFFER -> BoneWarm
    }
    val dotColor = avatarBg.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Avatar circle with center dot
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (notification.isUnread) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = Ink900,
            )
            Text(
                text = notification.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Time + unread dot
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = notification.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )
            if (notification.isUnread) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Coral),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    PawcareTheme {
        NotificationsScreen(onBack = {})
    }
}
