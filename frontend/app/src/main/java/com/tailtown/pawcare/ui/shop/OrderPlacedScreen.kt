package com.tailtown.pawcare.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun OrderPlacedScreen(
    orderId: String,
    petName: String = "your pet",
    amount: Int? = null,
    onViewOrder: () -> Unit,
    onContinueShopping: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        // Teal success circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Teal600.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Teal600.copy(alpha = 0.25f)),
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Order placed!",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$petName's goodies arrive tomorrow.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (amount != null) "Order #$orderId · ₹$amount" else "Order #$orderId",
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
        )

        Spacer(Modifier.height(32.dp))

        // Track delivery section
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Track delivery",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
            Spacer(Modifier.height(16.dp))
            TrackingTimeline(steps = sampleTrackingSteps)
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(32.dp))

        // Bottom action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onViewOrder,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = PillShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Ink900),
            ) {
                Text(
                    text = "View order",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Ink900,
                )
            }
            Button(
                onClick = onContinueShopping,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink900,
                    contentColor = White,
                ),
            ) {
                Text(
                    text = "Continue shopping",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TrackingTimeline(steps: List<DeliveryStep>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                // Left: dot + connector
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Status dot
                    when (step.status) {
                        StepStatus.DONE -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Teal600),
                            )
                        }
                        StepStatus.ACTIVE -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Coral)
                                    .border(3.dp, CoralSoft, CircleShape),
                            )
                        }
                        StepStatus.PENDING -> {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Hairline),
                            )
                        }
                    }
                    // Connector line — skip after last step
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(Hairline),
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Right: label + time label
                Column(modifier = Modifier.padding(top = 1.dp)) {
                    Text(
                        text = step.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = when (step.status) {
                                StepStatus.DONE, StepStatus.ACTIVE -> FontWeight.Medium
                                StepStatus.PENDING -> FontWeight.Normal
                            },
                        ),
                        color = when (step.status) {
                            StepStatus.DONE, StepStatus.ACTIVE -> Ink900
                            StepStatus.PENDING -> Ink500
                        },
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = step.timeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (step.status) {
                            StepStatus.ACTIVE -> Coral
                            else -> Ink500
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderPlacedScreenPreview() {
    PawcareTheme {
        OrderPlacedScreen(
            orderId = "VET2406",
            onViewOrder = {},
            onContinueShopping = {},
        )
    }
}
