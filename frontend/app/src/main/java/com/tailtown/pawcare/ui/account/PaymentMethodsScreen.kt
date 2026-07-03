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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun PaymentMethodsScreen(
    methods: List<SavedPaymentMethod> = samplePaymentMethods,
    onSetDefault: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onAddNew: () -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                "Payment methods",
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
            items(methods) { method ->
                PaymentCard(
                    method = method,
                    onSetDefault = { onSetDefault(method.id) },
                    onDelete = { onDelete(method.id) },
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onAddNew,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Ink900, contentColor = White),
                ) {
                    Text("+ Add payment method", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(method: SavedPaymentMethod, onSetDefault: () -> Unit, onDelete: () -> Unit) {
    val accentColor = when (method.type) {
        PaymentMethodType.UPI        -> Teal600
        PaymentMethodType.CARD       -> Ink900
        PaymentMethodType.NETBANKING -> Color(0xFF5C6BC0)
    }
    val typeLabel = when (method.type) {
        PaymentMethodType.UPI        -> "UPI"
        PaymentMethodType.CARD       -> "Card"
        PaymentMethodType.NETBANKING -> "Net banking"
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(typeLabel.first().toString(), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = accentColor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(method.label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink900)
                Text(method.masked, style = MaterialTheme.typography.labelSmall, color = Ink500)
            }
            if (method.isDefault) {
                Box(
                    modifier = Modifier.clip(PillShape).background(CoralSoft).padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("Default", style = MaterialTheme.typography.labelSmall, color = Coral)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!method.isDefault) {
                OutlinedButton(
                    onClick = onSetDefault,
                    shape = PillShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
                    modifier = Modifier.height(36.dp),
                ) {
                    Text("Set default", style = MaterialTheme.typography.labelSmall, color = Ink900)
                }
            }
            OutlinedButton(
                onClick = onDelete,
                shape = PillShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Hairline),
                modifier = Modifier.height(36.dp),
            ) {
                Text("Remove", style = MaterialTheme.typography.labelSmall, color = Ink500)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodsPreview() {
    PawcareTheme { PaymentMethodsScreen(onBack = {}) }
}
