package com.tailtown.pawcare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

private data class AccountNavItem(val label: String, val icon: ImageVector)

private val accountNavItems = listOf(
    AccountNavItem("Explore", Icons.Default.Search),
    AccountNavItem("Inbox", Icons.Default.Mail),
    AccountNavItem("Visits", Icons.Default.CalendarToday),
    AccountNavItem("Shop", Icons.Default.ShoppingBag),
    AccountNavItem("Me", Icons.Default.Person),
)

@Composable
fun AccountScreen(
    viewModel: AccountViewModel? = null,
    onViewPets: () -> Unit = {},
    onViewProfile: () -> Unit = {},
    onAddresses: () -> Unit = {},
    onSubscriptions: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onOrderHistory: () -> Unit = {},
    onReferFriend: () -> Unit = {},
    onHelpSupport: () -> Unit = {},
    onSettings: () -> Unit = {},
    onInbox: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onVisits: () -> Unit = {},
    onShop: () -> Unit = {},
) {
    val name by (viewModel?.name ?: kotlinx.coroutines.flow.MutableStateFlow("")).collectAsState()
    val phone by (viewModel?.phone ?: kotlinx.coroutines.flow.MutableStateFlow("")).collectAsState()
    val petCount by (viewModel?.petCount ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState()
    val subscriptions by (viewModel?.subscriptions ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList())).collectAsState()

    val displayName = name.ifBlank { "You" }
    val initials = displayName.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    val activeSubscriptions = subscriptions.count { it.isActive }

    Scaffold(
        containerColor = Bone,
        bottomBar = {
            NavigationBar(
                containerColor = White,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                accountNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == 4, // "Me" is always active
                        onClick = {
                            when (index) {
                                0 -> onNavigateHome()
                                1 -> onInbox()
                                2 -> onVisits()
                                3 -> onShop()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // ── Top white section ─────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .statusBarsPadding()
                        .padding(24.dp),
                ) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.displayLarge,
                        color = Ink900,
                    )

                    Spacer(Modifier.height(20.dp))

                    // Profile card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(White)
                            .border(1.dp, Hairline, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(CoralSoft),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = Coral,
                                )
                            }

                            Column {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = Ink900,
                                )
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Ink500,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "View profile",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        textDecoration = TextDecoration.Underline,
                                    ),
                                    color = Coral,
                                    modifier = Modifier.clickable { onViewProfile() },
                                )
                            }
                        }
                    }
                }
            }

            // ── Main menu card ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .border(1.dp, Hairline, RoundedCornerShape(16.dp)),
                ) {
                    MenuRow(
                        label = "My pets",
                        trailingContent = {
                            Text(
                                text = "$petCount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink500,
                            )
                        },
                        onClick = onViewPets,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Addresses",
                        trailingContent = null,
                        onClick = onAddresses,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Subscriptions",
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(CoralSoft)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "$activeSubscriptions active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Coral,
                                )
                            }
                        },
                        onClick = onSubscriptions,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Payment methods",
                        trailingContent = null,
                        onClick = onPaymentMethods,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Order history",
                        trailingContent = null,
                        onClick = onOrderHistory,
                    )
                }
            }

            // ── Secondary menu card ───────────────────────────────────────────
            item {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .border(1.dp, Hairline, RoundedCornerShape(16.dp)),
                ) {
                    MenuRow(
                        label = "Refer a friend",
                        trailingContent = {
                            Box(
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(CoralSoft)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "₹200 each",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Coral,
                                )
                            }
                        },
                        onClick = onReferFriend,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Help & support",
                        trailingContent = null,
                        onClick = onHelpSupport,
                    )
                    HorizontalDivider(color = Hairline, thickness = 1.dp)
                    MenuRow(
                        label = "Settings",
                        trailingContent = null,
                        onClick = onSettings,
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MenuRow(
    label: String,
    trailingContent: (@Composable () -> Unit)?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink900,
        )
        trailingContent?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountScreenPreview() {
    PawcareTheme {
        AccountScreen(viewModel = null)
    }
}
