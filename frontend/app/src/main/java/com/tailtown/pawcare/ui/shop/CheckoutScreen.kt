package com.tailtown.pawcare.ui.shop

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun CheckoutScreen(
    total: Int = 0,
    itemCount: Int = 0,
    deliveryAddress: String = "",
    phone: String = "",
    isPlacingOrder: Boolean = false,
    onBack: () -> Unit,
    onPlaceOrder: () -> Unit,
) {
    Scaffold(
        containerColor = Bone,
        topBar = {
            CheckoutTopBar(onBack = onBack)
        },
        bottomBar = {
            CheckoutBottomBar(
                total = total,
                itemCount = itemCount,
                isPlacingOrder = isPlacingOrder,
                onPlaceOrder = onPlaceOrder,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp,
                start = 20.dp,
                end = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // DELIVER TO
            item {
                SectionHeader(title = "DELIVER TO")
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(White)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Home",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = Ink900,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = deliveryAddress.ifBlank { "Add delivery address" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink500,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = phone.ifBlank { "Add phone number" },
                                style = MaterialTheme.typography.labelSmall,
                                color = Ink500,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            // Default badge
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall,
                                color = Coral,
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(CoralSoft)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Change",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Coral,
                                modifier = Modifier.clickable { },
                            )
                        }
                    }
                }
            }

            // DELIVERY SLOT
            item {
                SectionHeader(title = "DELIVERY SLOT")
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(White)
                        .padding(16.dp),
                ) {
                    val tomorrow = java.time.LocalDate.now().plusDays(1)
                        .format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM"))
                    Text(
                        text = "$tomorrow · 10 AM – 1 PM",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Standard delivery",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }
            }

            // PAYMENT
            item {
                SectionHeader(title = "PAYMENT")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Pay securely via UPI, card or netbanking — powered by Razorpay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink900,
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentVerifyingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Coral)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Verifying payment, please wait…",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
        }
    }
}

@Composable
fun PaymentPendingScreen(onGoToOrders: () -> Unit, ctaLabel: String = "Go to orders") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(CoralSoft),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Coral)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Confirming your payment…",
            style = MaterialTheme.typography.displaySmall,
            color = Ink900,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This is taking a little longer than usual. We'll notify you as soon as it's confirmed.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onGoToOrders,
            modifier = Modifier.height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(containerColor = Ink900, contentColor = White),
        ) {
            Text(
                text = ctaLabel,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Composable
fun PaymentFailedScreen(
    reason: String,
    cancelled: Boolean,
    onRetry: () -> Unit,
    onContactSupport: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(CoralSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "✕", style = MaterialTheme.typography.headlineMedium, color = Coral)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = if (cancelled) "Payment cancelled" else "Payment failed",
            style = MaterialTheme.typography.displaySmall,
            color = Ink900,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = reason,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = White),
        ) {
            Text(
                text = "Retry payment",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
        ) {
            Text(text = "Back to cart", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Contact support",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Coral,
            modifier = Modifier.clickable { onContactSupport() },
        )
    }
}

@Composable
private fun CheckoutTopBar(onBack: () -> Unit) {
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
            Text(
                text = "Checkout",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            // Balancing spacer to keep heading visually centered
            Spacer(Modifier.width(48.dp))
        }
        HorizontalDivider(color = Hairline)
    }
}

@Composable
private fun CheckoutBottomBar(total: Int, itemCount: Int, isPlacingOrder: Boolean, onPlaceOrder: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Hairline)
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Items ($itemCount)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                    Text(
                        text = "₹$total",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onPlaceOrder,
                enabled = !isPlacingOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Coral,
                    contentColor = White,
                ),
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "Pay ₹$total",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = Ink500,
    )
}

@Preview(showBackground = true)
@Composable
private fun CheckoutScreenPreview() {
    PawcareTheme {
        CheckoutScreen(
            onBack = {},
            onPlaceOrder = {},
        )
    }
}
