package com.tailtown.pawcare.ui.vet

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun VetDetailScreen(
    vet: Vet,
    onBack: () -> Unit,
    onReserve: (date: String, time: String) -> Unit,
    slotState: VetDetailViewModel.SlotSelectionState = VetDetailViewModel.SlotSelectionState(),
    onDateSelected: (Int) -> Unit = {},
    onTimeSelected: (Int) -> Unit = {},
) {
    val selectedDate = slotState.availableDates.getOrNull(slotState.selectedDateIdx)
    val selectedTime = slotState.timeSlotsForDate.getOrNull(slotState.selectedTimeIdx)
    val dateLabel = selectedDate?.let { "${it.dayNum} ${it.monthLabel}" } ?: ""
    val timeLabel = selectedTime ?: ""

    Scaffold(
        containerColor = Bone,
        bottomBar = {
            BookingBottomBar(
                price = vet.pricePerVisit,
                dateLabel = dateLabel,
                timeLabel = timeLabel,
                isBooking = slotState.isBooking,
                hasSlots = slotState.availableDates.isNotEmpty(),
                onReserve = { onReserve(dateLabel, timeLabel) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // Hero gallery
            item {
                val galleryImages = vet.images.ifEmpty { listOfNotNull(vet.imageUrl) }
                val pagerState = androidx.compose.foundation.pager.rememberPagerState { galleryImages.size.coerceAtLeast(1) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(vet.heroTint),
                ) {
                    if (galleryImages.isNotEmpty()) {
                        androidx.compose.foundation.pager.HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                        ) { page ->
                            AsyncImage(
                                model = galleryImages[page],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }

                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.7f)),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Ink900,
                        )
                    }

                    if (galleryImages.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            repeat(galleryImages.size) { idx ->
                                val isCurrent = idx == pagerState.currentPage
                                Box(
                                    modifier = Modifier
                                        .size(if (isCurrent) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrent) White else White.copy(alpha = 0.4f)),
                                )
                            }
                        }
                    }
                }
            }

            // Vet info
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Text(
                        text = vet.name,
                        style = MaterialTheme.typography.displayLarge,
                        color = Ink900,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${vet.specialty} · ${vet.fullLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "${vet.rating} · ${vet.reviewCount} reviews${if (vet.isSuperhost) " · " else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                        if (vet.isSuperhost) {
                            Text(
                                text = "Superhost",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = TextDecoration.Underline,
                                ),
                                color = Ink900,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = Hairline)
                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "${vet.yearsExperience} years of experience",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Ink900,
                    )
                    if (vet.certifications.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = vet.certifications,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = Hairline)
                }
            }

            // Date picker
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Pick a date",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink900,
                    )
                    Spacer(Modifier.height(14.dp))
                    if (slotState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Coral,
                            strokeWidth = 2.dp,
                        )
                    } else if (slotState.availableDates.isEmpty()) {
                        Text(
                            text = "No available dates",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(slotState.availableDates) { idx, slot ->
                                val selected = idx == slotState.selectedDateIdx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) Ink900 else White)
                                        .border(1.dp, if (selected) Ink900 else Hairline, RoundedCornerShape(12.dp))
                                        .clickable { onDateSelected(idx) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = slot.dayLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (selected) White.copy(alpha = 0.7f) else Ink500,
                                        )
                                        Text(
                                            text = "${slot.dayNum}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (selected) White else Ink900,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // Time slots
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Available slots",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink900,
                    )
                    Spacer(Modifier.height(14.dp))
                    if (!slotState.isLoading && slotState.timeSlotsForDate.isEmpty()) {
                        Text(
                            text = "No slots for this date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(slotState.timeSlotsForDate) { idx, time ->
                                val selected = idx == slotState.selectedTimeIdx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) Ink900 else White)
                                        .border(1.dp, if (selected) Ink900 else Hairline, RoundedCornerShape(12.dp))
                                        .clickable { onTimeSelected(idx) }
                                        .padding(horizontal = 20.dp, vertical = 12.dp),
                                ) {
                                    Text(
                                        text = time,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = if (selected) White else Ink900,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun BookingBottomBar(
    price: Int,
    dateLabel: String,
    timeLabel: String,
    isBooking: Boolean = false,
    hasSlots: Boolean = true,
    onReserve: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Hairline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "₹$price",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink900,
                )
                Text(
                    text = "$dateLabel · $timeLabel",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = Coral,
                )
            }
            Button(
                onClick = { if (!isBooking && hasSlots) onReserve() },
                enabled = hasSlots,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Coral,
                    contentColor = Color.White,
                    disabledContainerColor = Coral.copy(alpha = 0.4f),
                    disabledContentColor = Color.White,
                ),
                modifier = Modifier.height(48.dp),
            ) {
                if (isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "Reserve",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VetDetailPreview() {
    PawcareTheme { VetDetailScreen(vet = sampleVets.first(), onBack = {}, onReserve = { _, _ -> }) }
}
