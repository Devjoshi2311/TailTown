package com.tailtown.pawcare.ui.account

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun OrderHistoryScreen(
    orders: List<OrderSummary> = sampleOrders,
    onReorder: (String) -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                "Order history",
                style = MaterialTheme.typography.displayLarge,
                color = Ink900,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
            )
            HorizontalDivider(color = Hairline)
        }

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(orders) { order ->
                OrderCard(order = order, onReorder = { onReorder(order.id) })
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderSummary, onReorder: () -> Unit) {
    val (badgeBg, badgeText, statusLabel) = when (order.status) {
        OrderStatus.DELIVERED   -> Triple(Color(0xFFD6EFE8), Teal600,  "Delivered")
        OrderStatus.IN_TRANSIT  -> Triple(Color(0xFFFEF3C7), Amber600, "In transit")
        OrderStatus.PROCESSING  -> Triple(CoralSoft,         Coral,    "Processing")
        OrderStatus.CANCELLED   -> Triple(Color(0xFFF5F5F5), Ink500,   "Cancelled")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, Hairline, RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(order.id, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink900)
            Box(modifier = Modifier.clip(PillShape).background(badgeBg).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = badgeText)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(order.itemsLabel, style = MaterialTheme.typography.bodyMedium, color = Ink500, maxLines = 1)
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Hairline)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(order.dateLabel, style = MaterialTheme.typography.labelSmall, color = Ink500)
            Text("₹${order.total}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink900)
        }

        if (order.status != OrderStatus.CANCELLED) {
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(Bone)
                    .border(1.dp, Hairline, PillShape)
                    .clickable(onClick = onReorder)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Reorder", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = Ink900)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderHistoryPreview() {
    PawcareTheme { OrderHistoryScreen(onBack = {}) }
}
