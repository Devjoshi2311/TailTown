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
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.tailtown.pawcare.ui.theme.White

@Composable
fun AddressesScreen(
    addresses: List<Address> = sampleAddresses,
    onSetDefault: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onAddNew: () -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding(),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                text = "Addresses",
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
            items(addresses) { address ->
                AddressCard(
                    address = address,
                    onSetDefault = { onSetDefault(address.id) },
                    onDelete = { onDelete(address.id) },
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
                    Text("+ Add new address", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

@Composable
private fun AddressCard(address: Address, onSetDefault: () -> Unit, onDelete: () -> Unit) {
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
            Text(address.label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink900)
            if (address.isDefault) {
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(CoralSoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("Default", style = MaterialTheme.typography.labelSmall, color = Coral)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(address.street, style = MaterialTheme.typography.bodyMedium, color = Ink500)
        Text("${address.city} – ${address.pincode}", style = MaterialTheme.typography.bodyMedium, color = Ink500)
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!address.isDefault) {
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
private fun AddressesPreview() {
    PawcareTheme { AddressesScreen(onBack = {}) }
}
