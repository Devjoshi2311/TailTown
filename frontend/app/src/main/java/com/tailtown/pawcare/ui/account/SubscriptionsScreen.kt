package com.tailtown.pawcare.ui.account

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun SubscriptionsScreen(
    subscriptions: List<SubscriptionItem> = sampleSubscriptions,
    onToggle: (String) -> Unit = {},
    onBack: () -> Unit,
) {
    val active = subscriptions.count { it.isActive }

    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Row(
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Subscriptions", style = MaterialTheme.typography.displayLarge, color = Ink900)
                Box(
                    modifier = Modifier.clip(PillShape).background(CoralSoft).padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("$active active", style = MaterialTheme.typography.labelSmall, color = Coral)
                }
            }
            HorizontalDivider(color = Hairline)
        }

        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(subscriptions) { sub ->
                SubscriptionCard(sub = sub, onToggle = { onToggle(sub.id) })
            }
        }
    }
}

@Composable
private fun SubscriptionCard(sub: SubscriptionItem, onToggle: () -> Unit) {
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
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.productName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink900)
                Text(sub.variantLabel, style = MaterialTheme.typography.labelSmall, color = Ink500)
            }
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = sub.isActive,
                onCheckedChange = { onToggle() },
                modifier = Modifier.size(width = 44.dp, height = 24.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Teal600,
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Hairline,
                ),
            )
        }

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = Hairline)
        Spacer(Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Next delivery", style = MaterialTheme.typography.labelSmall, color = Ink500)
                Text(sub.nextDelivery, style = MaterialTheme.typography.bodyMedium, color = Ink900)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Per cycle", style = MaterialTheme.typography.labelSmall, color = Ink500)
                Text("₹${sub.pricePerCycle}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = if (sub.isActive) Teal600 else Ink500)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubscriptionsPreview() {
    PawcareTheme { SubscriptionsScreen(onBack = {}) }
}
