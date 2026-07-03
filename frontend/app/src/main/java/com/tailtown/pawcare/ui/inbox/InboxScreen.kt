package com.tailtown.pawcare.ui.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.White

private val inboxTabs = listOf("All", "Vets", "Orders")

private data class InboxNavItem(val label: String, val icon: ImageVector)

private val inboxNavItems = listOf(
    InboxNavItem("Explore", Icons.Default.Search),
    InboxNavItem("Inbox", Icons.Default.Mail),
    InboxNavItem("Visits", Icons.Default.CalendarToday),
    InboxNavItem("Shop", Icons.Default.ShoppingBag),
    InboxNavItem("Me", Icons.Default.Person),
)

@Composable
fun InboxScreen(
    conversations: List<Conversation>,
    selectedFilter: Int,
    onFilterChange: (Int) -> Unit,
    onConversationClick: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onVisits: () -> Unit,
    onShop: () -> Unit,
    onAccount: () -> Unit,
) {
    val selectedTab = selectedFilter

    val filteredConversations = remember(conversations, selectedTab) {
        when (selectedTab) {
            1 -> conversations.filter { it.type == ConversationType.VET }
            2 -> conversations.filter { it.type == ConversationType.ORDER }
            else -> conversations
        }
    }

    Scaffold(
        containerColor = Bone,
        bottomBar = {
            NavigationBar(
                containerColor = White,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                inboxNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == 1,
                        onClick = {
                            when (index) {
                                0 -> onNavigateHome()
                                2 -> onVisits()
                                3 -> onShop()
                                4 -> onAccount()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Coral,
                            selectedTextColor = Coral,
                            unselectedIconColor = Ink500,
                            unselectedTextColor = Ink500,
                            indicatorColor = CoralSoft,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // ── White header ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding(),
            ) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Inbox",
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink900,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(Modifier.height(12.dp))

                // Filter tab row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = White,
                    contentColor = Ink900,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Coral),
                        )
                    },
                    divider = { HorizontalDivider(color = Hairline) },
                ) {
                    inboxTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTab,
                            onClick = { onFilterChange(index) },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (index == selectedTab) FontWeight.Medium else FontWeight.Normal,
                                    ),
                                    color = if (index == selectedTab) Ink900 else Ink500,
                                )
                            },
                        )
                    }
                }
            }

            // ── Conversation list ──────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            ) {
                items(filteredConversations) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation.id) },
                    )
                    HorizontalDivider(color = Hairline)
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar with optional unread dot
        Box(modifier = Modifier.size(44.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(conversation.avatarTint),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = conversation.name.first().toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
            }
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Coral)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Name + last message
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        // Time + unread badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = conversation.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )
            if (conversation.unreadCount > 0) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Coral),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${conversation.unreadCount} new",
                        style = MaterialTheme.typography.labelSmall,
                        color = Coral,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InboxScreenPreview() {
    PawcareTheme {
        InboxScreen(
            conversations = sampleConversations,
            selectedFilter = 0,
            onFilterChange = {},
            onConversationClick = {},
            onNavigateHome = {},
            onVisits = {},
            onShop = {},
            onAccount = {},
        )
    }
}
