package com.tailtown.pawcare.ui.health

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun PrescriptionScreen(
    prescriptionId: String,
    prescription: PrescriptionRecord = samplePrescription,
    onMarkDose: (String) -> Unit = {},
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = Bone,
        topBar = {
            PrescriptionTopBar(onBack = onBack)
        },
        bottomBar = {
            PrescriptionBottomBar(
                onMarkDose = { onMarkDose(prescription.doses.firstOrNull { !it.taken }?.time ?: "") },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── 1. Status badge + drug name card ──────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                ) {
                    // Active badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = Teal600.copy(alpha = 0.12f),
                                shape = PillShape,
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Active · ${prescription.daysLeft} days left",
                            style = MaterialTheme.typography.labelSmall,
                            color = Teal600,
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = prescription.name,
                        style = MaterialTheme.typography.displayLarge,
                        color = Ink900,
                    )

                    Text(
                        text = prescription.reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Hairline)
                    Spacer(Modifier.height(16.dp))

                    DetailRow(label = "Dosage", value = prescription.dosage)
                    Spacer(Modifier.height(10.dp))
                    DetailRow(label = "Frequency", value = prescription.frequency)
                    Spacer(Modifier.height(10.dp))
                    DetailRow(label = "Duration", value = prescription.duration)
                    Spacer(Modifier.height(10.dp))
                    DetailRow(label = "Prescribed", value = prescription.prescribedBy)
                }
            }

            // ── 2. Today's doses ─────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Today's doses",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Morning dose
                        DoseButton(
                            time = prescription.doses[0].time,
                            taken = prescription.doses[0].taken,
                            modifier = Modifier.weight(1f),
                            onClick = { onMarkDose(prescription.doses[0].time) },
                        )
                        // Evening dose
                        DoseButton(
                            time = prescription.doses[1].time,
                            taken = prescription.doses[1].taken,
                            modifier = Modifier.weight(1f),
                            onClick = { onMarkDose(prescription.doses[1].time) },
                        )
                    }
                }
            }

            // ── 3. Refill card ────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Bone,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Refill needed in ${prescription.refillDaysLeft} days",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )
                    Text(
                        text = "Auto-order from pet mall",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrescriptionTopBar(onBack: () -> Unit) {
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
                text = "Prescription",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900,
            )
        }
        HorizontalDivider(color = Hairline)
    }
}

@Composable
private fun PrescriptionBottomBar(onMarkDose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Hairline)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            Button(
                onClick = onMarkDose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink900,
                    contentColor = White,
                ),
            ) {
                Text(
                    text = "Mark 8 PM dose given",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
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
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Ink900,
        )
    }
}

@Composable
private fun DoseButton(
    time: String,
    taken: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (taken) {
        Button(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal600,
                contentColor = White,
            ),
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    } else {
        Box(
            modifier = modifier
                .height(52.dp)
                .background(CoralSoft, PillShape)
                .border(1.dp, Coral, PillShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Coral,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrescriptionScreenPreview() {
    PawcareTheme {
        PrescriptionScreen(
            prescriptionId = "rx1",
            onBack = {},
            onMarkDose = {},
        )
    }
}
