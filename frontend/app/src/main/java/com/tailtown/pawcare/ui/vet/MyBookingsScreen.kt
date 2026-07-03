package com.tailtown.pawcare.ui.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Amber600
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White
import kotlinx.coroutines.launch

private val tabs = listOf("Upcoming", "Past", "Cancelled")

private data class NavItem(val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("Explore", Icons.Default.Search),
    NavItem("Inbox", Icons.Default.Mail),
    NavItem("Visits", Icons.Default.CalendarToday),
    NavItem("Shop", Icons.Default.ShoppingBag),
    NavItem("Me", Icons.Default.Person),
)

private val activeStatuses    = setOf("CONFIRMED", "PENDING_PAYMENT")
private val cancelledStatuses = setOf("CANCELLED")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    bookings: List<UpcomingBooking>,
    onCancelBooking: (bookingId: String, version: Long) -> Unit = { _, _ -> },
    onNavigateHome: () -> Unit = {},
    onInbox: () -> Unit = {},
    onShop: () -> Unit = {},
    onPetProfile: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedNav by remember { mutableIntStateOf(2) }
    var pendingCancel by remember { mutableStateOf<UpcomingBooking?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val now = System.currentTimeMillis()
    val upcoming  = remember(bookings) { bookings.filter { it.status in activeStatuses && it.scheduledStartEpoch > now } }
    val past      = remember(bookings) { bookings.filter { it.status !in cancelledStatuses && it.scheduledStartEpoch in 1..now } }
    val cancelled = remember(bookings) { bookings.filter { it.status in cancelledStatuses } }

    // Cancel confirmation bottom sheet
    if (pendingCancel != null) {
        val booking = pendingCancel!!
        ModalBottomSheet(
            onDismissRequest = { pendingCancel = null },
            sheetState = sheetState,
            containerColor = White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            CancelConfirmSheet(
                booking = booking,
                onKeep = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        pendingCancel = null
                    }
                },
                onConfirmCancel = {
                    onCancelBooking(booking.bookingId, booking.version)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        pendingCancel = null
                    }
                },
            )
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
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedNav,
                        onClick = {
                            selectedNav = index
                            when (index) {
                                0 -> onNavigateHome()
                                1 -> onInbox()
                                3 -> onShop()
                                4 -> onPetProfile()
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
                            Text(text = item.label, style = MaterialTheme.typography.labelSmall)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Visits",
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink900,
                )
                Spacer(Modifier.height(16.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = White,
                    contentColor = Coral,
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
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTab,
                            onClick = { selectedTab = index },
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

            val displayList = when (selectedTab) {
                0    -> upcoming
                1    -> past
                else -> cancelled
            }

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Text(
                        text = "No ${tabs[selectedTab].lowercase()} visits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(displayList, key = { it.bookingId }) { booking ->
                        BookingCard(
                            booking = booking,
                            showCancel = selectedTab == 0,
                            onCancel = { pendingCancel = booking },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CancelConfirmSheet(
    booking: UpcomingBooking,
    onKeep: () -> Unit,
    onConfirmCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Warning icon circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Coral.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Coral,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Cancel appointment?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Ink900,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "This will cancel your visit with ${booking.doctorName}",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        // Booking summary pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Bone)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = booking.doctorName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Ink900,
                )
                Text(
                    text = booking.purpose,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = booking.dateLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Ink900,
                )
                Text(
                    text = booking.timeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Policy note
        Text(
            text = "Cancellations made less than 24 hours before the appointment may not be eligible for a refund.",
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        Spacer(Modifier.height(24.dp))

        // Confirm cancel — destructive
        Button(
            onClick = onConfirmCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Coral,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = "Yes, cancel appointment",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(10.dp))

        // Keep — neutral
        OutlinedButton(
            onClick = onKeep,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
        ) {
            Text(
                text = "Keep appointment",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Ink900,
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: UpcomingBooking,
    showCancel: Boolean = false,
    onCancel: () -> Unit = {},
) {
    val badgeBg = when (booking.urgencyStyle) {
        UrgencyStyle.CORAL -> CoralSoft
        UrgencyStyle.AMBER -> Color(0xFFFEF3C7)
    }
    val badgeText = when (booking.urgencyStyle) {
        UrgencyStyle.CORAL -> Coral
        UrgencyStyle.AMBER -> Amber600
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, Hairline, RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(PillShape)
                .background(badgeBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(badgeText),
                )
                Text(
                    text = booking.urgencyLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeText,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = booking.doctorName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Ink900,
        )
        Text(
            text = booking.purpose,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = booking.dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink500,
            )
            Text(
                text = booking.timeLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink500,
            )
        }

        if (showCancel && !booking.isVideo) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = PillShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Coral.copy(alpha = 0.5f)),
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Coral,
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Ink900,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        text = "Directions",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyBookingsPreview() {
    PawcareTheme {
        MyBookingsScreen(
            bookings = sampleBookings,
            onNavigateHome = {},
            onInbox = {},
            onShop = {},
            onPetProfile = {},
        )
    }
}
