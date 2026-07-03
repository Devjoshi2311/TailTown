package com.tailtown.pawcare.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun CartScreen(
    items: List<CartItem> = sampleCartItems,
    subtotal: Int = 1667,
    subscriptionSaving: Int = 130,
    total: Int = 1537,
    onIncrement: (String) -> Unit = {},
    onDecrement: (String) -> Unit = {},
    onBack: () -> Unit,
    onCheckout: () -> Unit,
) {
    Scaffold(
        containerColor = Bone,
        topBar = {
            CartTopBar(onBack = onBack)
        },
        bottomBar = {
            CartBottomBar(total = total, onCheckout = onCheckout)
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 12.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── 1. Cart items ─────────────────────────────────────────────────
            items(items) { item ->
                CartItemCard(
                    item = item,
                    qty = item.qty,
                    onIncrement = { onIncrement(item.product.id) },
                    onDecrement = { onDecrement(item.product.id) },
                )
            }

            // ── 2. Promo code row ─────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(White)
                        .border(
                            width = 1.dp,
                            color = Hairline,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { /* open promo input */ }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Apply promo code",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Ink500,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // ── 3. Order summary ──────────────────────────────────────────────
            item {
                OrderSummaryCard(
                    subtotal = subtotal,
                    subscriptionSaving = subscriptionSaving,
                    total = total,
                )
            }
        }
    }
}

@Composable
private fun CartTopBar(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink900,
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Your cart",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900,
            )
        }
        HorizontalDivider(color = Hairline)
    }
}

@Composable
private fun CartBottomBar(total: Int, onCheckout: () -> Unit) {
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "₹${total}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Ink900,
                )
                Text(
                    text = "View breakdown",
                    style = MaterialTheme.typography.labelSmall.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = Ink500,
                )
            }
            Button(
                onClick = onCheckout,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink900,
                    contentColor = White,
                ),
                modifier = Modifier.height(48.dp),
            ) {
                Text(
                    text = "Checkout",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    qty: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.product.heroTint),
            ) {
                if (item.product.imageUrl != null) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Spacer(Modifier.width(12.dp))

            // Name + variant label
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Ink900,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.variantLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Price + qty stepper
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${item.unitPrice * qty}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Ink900,
                )
                Spacer(Modifier.height(6.dp))
                QtyStepper(
                    qty = qty,
                    onDecrement = onDecrement,
                    onIncrement = onIncrement,
                )
            }
        }
    }
}

@Composable
private fun QtyStepper(
    qty: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Bone)
                .clickable(onClick = onDecrement),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "−",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = Ink900,
            )
        }
        Text(
            text = "$qty",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = Ink900,
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Bone)
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = Ink900,
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Int,
    subscriptionSaving: Int,
    total: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SummaryRow(label = "Subtotal", value = "₹${subtotal}")
        SummaryRow(label = "Delivery", value = "Free", valueColor = Teal600)
        SummaryRow(label = "Subscription saving", value = "−₹${subscriptionSaving}", valueColor = Teal600)
        HorizontalDivider(color = Hairline)
        SummaryRow(
            label = "Total",
            value = "₹${total}",
            labelBold = true,
            valueBold = true,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelBold: Boolean = false,
    valueBold: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = Ink900,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (labelBold) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (labelBold) Ink900 else Ink500,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = valueColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    PawcareTheme {
        CartScreen(onBack = {}, onCheckout = {})
    }
}
