package com.tailtown.pawcare.ui.shop

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun CategoryScreen(
    categoryLabel: String,
    filters: List<CategoryViewModel.FilterChip>,
    selectedFilter: String,
    products: List<ShopProduct>,
    isLoading: Boolean,
    onFilterSelect: (CategoryViewModel.FilterChip) -> Unit,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ── White header ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink900,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onBack() }
                        .padding(8.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = categoryLabel,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink900,
                )
            }
            HorizontalDivider(color = Hairline, thickness = 1.dp)
        }

        // ── Content area ──────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(12.dp))

            // Filter chips — driven by backend sub-categories
            if (filters.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filters) { chip ->
                        val selected = chip.label == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(if (selected) Ink900 else White)
                                .then(
                                    if (!selected) Modifier.border(1.dp, Hairline, PillShape)
                                    else Modifier
                                )
                                .clickable { onFilterSelect(chip) }
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                        ) {
                            Text(
                                text = chip.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) White else Ink900,
                                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Count row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (isLoading) "Loading…" else "${products.size} products",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Coral)
                }
            } else {
                // Product grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(products) { product ->
                        CategoryProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryProductCard(product: ShopProduct, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Hairline), RoundedCornerShape(16.dp))
            .background(White)
            .clickable { onClick() },
    ) {
        // Hero box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(product.heroTint),
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // Discount badge (top-left)
            if (product.discountPct != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(PillShape)
                        .background(Coral)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "-${product.discountPct}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = White,
                    )
                }
            }
            // Bookmark/heart icon placeholder (top-right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.55f)),
            )
        }

        // Product info
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = product.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
                maxLines = 1,
            )
            Spacer(Modifier.height(6.dp))
            // Rating row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Coral,
                    modifier = Modifier.size(11.dp),
                )
                Spacer(Modifier.width(3.dp))
                val reviewK = if (product.reviewCount >= 1000) {
                    "${product.reviewCount / 1000}.${(product.reviewCount % 1000) / 100}k"
                } else {
                    "${product.reviewCount}"
                }
                Text(
                    text = "${product.rating} · $reviewK",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "₹${product.price}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryScreenPreview() {
    PawcareTheme {
        CategoryScreen(
            categoryLabel = "Food",
            filters = listOf(
                CategoryViewModel.FilterChip("All", null),
                CategoryViewModel.FilterChip("Dry Food", "dry-id"),
                CategoryViewModel.FilterChip("Wet Food", "wet-id"),
                CategoryViewModel.FilterChip("Treats", "treats-id"),
            ),
            selectedFilter = "All",
            products = sampleProducts,
            isLoading = false,
            onFilterSelect = {},
            onBack = {},
            onProductClick = {},
        )
    }
}
