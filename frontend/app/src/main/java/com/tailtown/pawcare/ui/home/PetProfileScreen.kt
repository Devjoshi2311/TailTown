package com.tailtown.pawcare.ui.home

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun PetProfileScreen(
    petName: String = "your pet",
    petBreed: String = "",
    petGender: String = "",
    petAge: String = "",
    petWeight: String = "",
    onBookCheckup: () -> Unit,
    onViewVaccines: () -> Unit = {},
    onViewTimeline: () -> Unit = {},
    onViewWeight: () -> Unit = {},
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        // ── Avatar with edit dot ──────────────────────────────────────────────
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(CoralSoft),
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Ink900),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = petName,
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )

        Spacer(Modifier.height(4.dp))

        val breedLine = listOf(petBreed, petGender).filter { it.isNotBlank() }.joinToString(" · ")
        if (breedLine.isNotBlank()) {
            Text(
                text = breedLine,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink500,
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Stat chips ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatChip(value = petAge.ifBlank { "—" }, label = "age", modifier = Modifier.weight(1f))
            StatChip(value = petWeight.ifBlank { "—" }, label = "weight", modifier = Modifier.weight(1f).clickable { onViewWeight() })
        }

        Spacer(Modifier.height(32.dp))

        // ── Health section ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Health",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )

            Spacer(Modifier.height(12.dp))

            HealthCard(
                title = "Vaccines",
                subtitle = "All up to date",
                accentColor = Teal600,
                onClick = onViewVaccines,
            )

            Spacer(Modifier.height(10.dp))

            HealthCard(
                title = "Check-up due",
                subtitle = "In 12 days",
                accentColor = Amber600,
                onClick = onViewTimeline,
            )
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(32.dp))

        // ── CTA ───────────────────────────────────────────────────────────────
        Button(
            onClick = onBookCheckup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Ink900,
                contentColor = White,
            ),
        ) {
            Text(
                text = "Book a check-up",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Ink900,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
        )
    }
}

@Composable
private fun HealthCard(title: String, subtitle: String, accentColor: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Coloured accent tile
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.35f)),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Ink900,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetProfileScreenPreview() {
    PawcareTheme {
        PetProfileScreen(onBookCheckup = {}, onBack = {})
    }
}
