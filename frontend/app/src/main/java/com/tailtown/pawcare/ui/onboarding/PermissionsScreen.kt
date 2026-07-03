package com.tailtown.pawcare.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.BoneWarm
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

private val featureChips = listOf(
    "Show vets within 5 km",
    "Same-day delivery checks",
    "Pickup & visit reminders",
)

@Composable
fun PermissionsScreen(
    onAllowLocation: () -> Unit,
    onNotNow: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(Modifier.height(32.dp))

        // Warm hero plate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(BoneWarm),
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Nearby vets,\nfaster delivery.",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Share your location so we can show vets nearby and estimate delivery to your door.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )

        Spacer(Modifier.height(24.dp))

        // Feature chips
        featureChips.forEach { label ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .border(1.dp, Hairline, RoundedCornerShape(12.dp))
                    .background(White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onAllowLocation,
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
                text = "Allow location",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(14.dp))

        TextButton(
            onClick = onNotNow,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = "Not now",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline,
                ),
                color = Ink500,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionsScreenPreview() {
    PawcareTheme { PermissionsScreen(onAllowLocation = {}, onNotNow = {}) }
}
