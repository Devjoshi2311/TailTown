package com.tailtown.pawcare.ui.vet

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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
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

private val vetFilters = listOf("All", "Home visit", "Surgery", "Dental", "Small animals")

@Composable
fun VetDirectoryScreen(
    vets: List<Vet>,
    city: String = "",
    onVetClick: (String) -> Unit,
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredVets = remember(vets, selectedFilter) {
        when (selectedFilter) {
            "Home visit" -> vets.filter { it.homeVisitAvailable }
            "All"        -> vets
            else         -> vets.filter { it.specialty.contains(selectedFilter, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding(),
    ) {
        Spacer(Modifier.height(16.dp))

        // Location chip
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .border(1.dp, Hairline, PillShape)
                    .background(White, PillShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Ink900,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = run {
                        val dateStr = java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("EEE d MMM"))
                        if (city.isNotBlank()) "$city · $dateStr" else dateStr
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(vetFilters) { filter ->
                val selected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(if (selected) Ink900 else White)
                        .border(1.dp, if (selected) Ink900 else Hairline, PillShape)
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selected) White else Ink900,
                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "${filteredVets.size} vets available",
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(filteredVets) { vet ->
                VetCard(vet = vet, onClick = { onVetClick(vet.id) })
            }
        }
    }
}

@Composable
private fun VetCard(vet: Vet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // Hero gallery
            if (vet.images.isNotEmpty()) {
                val pagerState = androidx.compose.foundation.pager.rememberPagerState { vet.images.size }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(vet.heroTint),
                ) {
                    androidx.compose.foundation.pager.HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        AsyncImage(
                            model = vet.images[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        repeat(vet.images.size) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx == pagerState.currentPage) 6.dp else 4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        White.copy(alpha = if (idx == pagerState.currentPage) 1f else 0.55f),
                                    ),
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(vet.heroTint),
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                if (vet.isSuperhost) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Hairline, PillShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Superhost vet",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink900,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = vet.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Ink900,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = "${vet.rating} (${vet.reviewCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink900,
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${vet.location} · ${vet.specialty}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
                Text(
                    text = "${vet.yearsExperience} years · ${vet.languages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "₹${vet.pricePerVisit} / visit${if (vet.homeVisitAvailable) " · Home visit available" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VetDirectoryPreview() {
    PawcareTheme { VetDirectoryScreen(vets = sampleVets, onVetClick = {}) }
}
