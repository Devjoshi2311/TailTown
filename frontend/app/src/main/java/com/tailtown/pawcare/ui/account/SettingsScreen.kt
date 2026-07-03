package com.tailtown.pawcare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.firebase.BuildConfig
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun SettingsScreen(
    prefs: NotificationPrefs = NotificationPrefs(),
    onAppointmentNotif: (Boolean) -> Unit = {},
    onMedicationNotif: (Boolean) -> Unit = {},
    onOrderNotif: (Boolean) -> Unit = {},
    onPromoNotif: (Boolean) -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                "Settings",
                style = MaterialTheme.typography.displayLarge,
                color = Ink900,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
            )
            HorizontalDivider(color = Hairline)
        }

        LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text(
                    "Notifications",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .border(1.dp, Hairline, RoundedCornerShape(16.dp)),
                ) {
                    ToggleRow(
                        label = "Appointment reminders",
                        subtitle = "Get reminded before each vet visit",
                        checked = prefs.appointments,
                        onCheckedChange = onAppointmentNotif,
                    )
                    HorizontalDivider(color = Hairline)
                    ToggleRow(
                        label = "Medication alerts",
                        subtitle = "Dose time reminders for prescriptions",
                        checked = prefs.medications,
                        onCheckedChange = onMedicationNotif,
                    )
                    HorizontalDivider(color = Hairline)
                    ToggleRow(
                        label = "Order updates",
                        subtitle = "Dispatch, out-for-delivery, delivered",
                        checked = prefs.orders,
                        onCheckedChange = onOrderNotif,
                    )
                    HorizontalDivider(color = Hairline)
                    ToggleRow(
                        label = "Promotions & offers",
                        subtitle = "Deals, coupons, new product launches",
                        checked = prefs.promotions,
                        onCheckedChange = onPromoNotif,
                    )
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "App version ${BuildConfig.VERSION_NAME} · TailTown",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = Ink900)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Ink500)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(width = 44.dp, height = 24.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Teal600,
                uncheckedThumbColor = White,
                uncheckedTrackColor = Hairline,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    PawcareTheme { SettingsScreen(onBack = {}) }
}
