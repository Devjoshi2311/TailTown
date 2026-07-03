package com.tailtown.pawcare.ui.health

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

private val filterChips = listOf("All", "Visits", "Vaccines", "Rx")

private val dotColorFor: (TimelineEntryType) -> Color = { type ->
    when (type) {
        TimelineEntryType.CHECK_UP -> Teal600
        TimelineEntryType.VACCINE -> Teal600
        TimelineEntryType.PRESCRIPTION -> Color(0xFF7B6CF4)
    }
}

private fun TimelineEntry.matchesFilter(chipIndex: Int): Boolean = when (chipIndex) {
    0 -> true
    1 -> type == TimelineEntryType.CHECK_UP
    2 -> type == TimelineEntryType.VACCINE
    3 -> type == TimelineEntryType.PRESCRIPTION
    else -> true
}

@Composable
fun MedicalTimelineScreen(
    petName: String = "your pet",
    onBack: () -> Unit,
    onViewPrescription: (String) -> Unit = {},
) {
    var selectedChip by remember { mutableIntStateOf(0) }

    val filteredGroups = sampleTimelineGroups
        .map { group ->
            group.copy(entries = group.entries.filter { it.matchesFilter(selectedChip) })
        }
        .filter { it.entries.isNotEmpty() }

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
                    text = "${petName}'s history",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink900,
                )
            }

            // Filter chips row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(filterChips) { index, label ->
                    val selected = index == selectedChip
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (selected) Ink900 else White)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = if (selected) Color.Transparent else Hairline,
                                shape = PillShape,
                            )
                            .clickable { selectedChip = index }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) White else Ink900,
                        )
                    }
                }
            }

            // Timeline content + bottom button
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                filteredGroups.forEachIndexed { groupIndex, group ->
                    // Month label
                    item {
                        if (groupIndex > 0) {
                            Spacer(Modifier.height(0.dp)) // spacedBy(16.dp) already handles spacing
                        }
                        Text(
                            text = group.monthLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink500,
                        )
                    }

                    // Entries for this group
                    items(
                        items = group.entries,
                        key = { it.id },
                    ) { entry ->
                        TimelineEntryCard(
                            entry = entry,
                            onClick = {
                                if (entry.type == TimelineEntryType.PRESCRIPTION) {
                                    onViewPrescription(entry.id)
                                }
                            },
                        )
                    }
                }

                // "Add a record" button
                item {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = PillShape,
                        border = BorderStroke(1.dp, Ink900),
                        contentPadding = PaddingValues(24.dp),
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
private fun TimelineEntryCard(
    entry: TimelineEntry,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .border(1.dp, Hairline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // Colored type dot
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dotColorFor(entry.type)),
            )

            Spacer(Modifier.width(12.dp))

            // Main content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Ink900,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )

                // Optional note card
                entry.note?.let { note ->
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F7F4))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Date label
            Text(
                text = entry.dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicalTimelinePreview() {
    PawcareTheme { MedicalTimelineScreen(onBack = {}) }
}
