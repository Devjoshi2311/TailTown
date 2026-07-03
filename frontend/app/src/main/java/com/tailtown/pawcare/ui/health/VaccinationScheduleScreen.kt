package com.tailtown.pawcare.ui.health

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun VaccinationScheduleScreen(
    petName: String = "your pet",
    onBack: () -> Unit,
    onBookNow: () -> Unit = {},
) {
    val upcomingVaccines = sampleVaccineRecords.filter {
        it.status == VaccineStatus.DUE || it.status == VaccineStatus.UPCOMING
    }
    val completedVaccines = sampleVaccineRecords.filter {
        it.status == VaccineStatus.COMPLETED
    }

    Scaffold(containerColor = Bone) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // White top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Ink900,
                    )
                }
                Text(
                    text = "Vaccines",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink900,
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Fully vaccinated banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Teal600.copy(alpha = 0.12f))
                            .border(1.dp, Teal600.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Column {
                            Text(
                                text = "$petName is fully vaccinated",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = Teal600,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Next due in 12 days",
                                style = MaterialTheme.typography.labelSmall,
                                color = Teal600.copy(alpha = 0.7f),
                            )
                        }
                    }
                }

                // 2. "UP NEXT" section label
                item {
                    Text(
                        text = "UP NEXT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }

                // 3. Upcoming vaccine cards
                items(upcomingVaccines) { vaccine ->
                    VaccineUpcomingCard(
                        vaccine = vaccine,
                        onBookNow = onBookNow,
                    )
                }

                // 4. "COMPLETED" section label
                item {
                    Text(
                        text = "COMPLETED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }

                // 5. Completed vaccine rows inside a single white card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(White)
                            .border(1.dp, Hairline, RoundedCornerShape(12.dp)),
                    ) {
                        Column {
                            completedVaccines.forEachIndexed { index, vaccine ->
                                VaccineCompletedRow(vaccine = vaccine)
                                if (index < completedVaccines.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .height(1.dp)
                                            .background(Hairline),
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. "Add a record" outlined button
                item {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = PillShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Ink900),
                    ) {
                        Text(
                            text = "Add a record",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                            ),
                            color = Ink900,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VaccineUpcomingCard(
    vaccine: VaccineRecord,
    onBookNow: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Hairline, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vaccine.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = buildString {
                            append(vaccine.frequency)
                            vaccine.dueDateLabel?.let { append(" · $it") }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }

                // Status badge
                when (vaccine.status) {
                    VaccineStatus.DUE -> DueBadge()
                    VaccineStatus.UPCOMING -> UpcomingBadge(
                        label = vaccine.dueDateLabel?.let { "In 5 mo" } ?: "Upcoming",
                    )
                    else -> {}
                }
            }

            // "Book now" button only for DUE vaccines
            if (vaccine.status == VaccineStatus.DUE) {
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onBookNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Coral,
                        contentColor = White,
                    ),
                ) {
                    Text(
                        text = "Book now",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DueBadge() {
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Amber600),
            )
            Text(
                text = "Due",
                style = MaterialTheme.typography.labelSmall,
                color = Amber600,
            )
        }
    }
}

@Composable
private fun UpcomingBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(Bone)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
        )
    }
}

@Composable
private fun VaccineCompletedRow(vaccine: VaccineRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = vaccine.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink900,
            )
            Spacer(Modifier.height(2.dp))
            val detail = buildString {
                vaccine.givenDateLabel?.let { append("Given $it") }
                vaccine.vet?.let { append(" · $it") }
            }
            if (detail.isNotEmpty()) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VaccinationSchedulePreview() {
    PawcareTheme { VaccinationScheduleScreen(onBack = {}) }
}
