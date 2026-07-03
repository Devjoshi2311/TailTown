package com.tailtown.pawcare.ui.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun BookedScreen(
    vet: Vet,
    petName: String = "your pet",
    bookingWhen: String = "",
    onAddToCalendar: () -> Unit,
    onMyBookings: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        // Success circle
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Teal600.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Teal600.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Teal600,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "You're booked!",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$petName's appointment is confirmed.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )

        Spacer(Modifier.height(28.dp))

        // Booking summary card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(20.dp),
        ) {
            // Vet row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CoralSoft),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = vet.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Ink900,
                    )
                    Text(
                        text = vet.specialty,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Hairline)
            Spacer(Modifier.height(16.dp))

            BookingRow(label = "When", value = bookingWhen.ifBlank { "—" })
            Spacer(Modifier.height(12.dp))
            BookingRow(label = "Where", value = "${vet.location} clinic")
            Spacer(Modifier.height(12.dp))
            BookingRow(label = "For", value = "$petName · Check-up")
            Spacer(Modifier.height(12.dp))
            BookingRow(label = "Paid", value = "₹${vet.pricePerVisit} · UPI")
        }

        Spacer(Modifier.height(28.dp))

        // What's next
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "What's next",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
            Spacer(Modifier.height(12.dp))
            NextItem(
                title = "Get directions",
                subtitle = "2.4 km from home",
            )
            HorizontalDivider(color = Hairline, modifier = Modifier.padding(vertical = 4.dp))
            NextItem(
                title = "Message ${vet.name}",
                subtitle = "Share Bruno's records",
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onAddToCalendar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Ink900,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = "View my bookings",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun BookingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Ink900,
        )
    }
}

@Composable
private fun NextItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Bone),
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Ink900,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Ink500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookedScreenPreview() {
    PawcareTheme { BookedScreen(vet = sampleVets.first(), onAddToCalendar = {}, onMyBookings = {}) }
}
