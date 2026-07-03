package com.tailtown.pawcare.ui.shop

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextDecoration
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
fun ProductDetailScreen(
    product: ShopProduct,
    onBack: () -> Unit,
    onAddToCart: () -> Unit,
) {
    var selectedVariantIdx by remember { mutableIntStateOf(0) }
    var subscribeEnabled by remember { mutableStateOf(false) }

    val displayPrice = if (subscribeEnabled && product.discountPct != null)
        (product.price * (100 - product.discountPct) / 100)
    else product.price

    Scaffold(
        containerColor = Bone,
        bottomBar = {
            ProductDetailBottomBar(
                price = displayPrice,
                onAddToCart = onAddToCart,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // ── 1. Hero pager ─────────────────────────────────────────────────
            item {
                val pagerState = rememberPagerState(pageCount = { 4 })
                Box {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
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
                        }
                    }

                    // Back button overlay
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

                    // Pager indicator dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(4) { idx ->
                            val isCurrent = idx == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .size(if (isCurrent) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCurrent) Ink900 else Ink900.copy(alpha = 0.25f),
                                    ),
                            )
                        }
                    }
                }
            }

            // ── 2. Product info ───────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                ) {
                    // Bestseller badge
                    if (product.isBestseller) {
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(CoralSoft)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = "Bestseller",
                                style = MaterialTheme.typography.labelSmall,
                                color = Coral,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.displayLarge,
                        color = Ink900,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink500,
                    )
                    Spacer(Modifier.height(10.dp))

                    // Rating row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = "${product.rating} · ${product.reviewCount} reviews",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.Underline,
                            ),
                            color = Ink500,
                        )
                    }
                }
            }

            // ── 3. Divider ────────────────────────────────────────────────────
            item {
                HorizontalDivider(color = Hairline)
            }

            // ── 4. Pack size ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = "Pack size",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Ink900,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(product.subtitle).forEachIndexed { idx, label ->
                            val selected = idx == selectedVariantIdx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Ink900 else White)
                                    .then(
                                        if (!selected) Modifier.border(1.dp, Ink900, RoundedCornerShape(10.dp))
                                        else Modifier,
                                    )
                                    .clickable { selectedVariantIdx = idx }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = if (selected) White else Ink900,
                                )
                            }
                        }
                    }
                }
            }

            // ── 5. Subscribe & save ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Teal600.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = Teal600.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "Auto-deliver monthly",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    color = Ink900,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Save ${product.discountPct ?: 10}% with auto-delivery",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Teal600,
                                )
                            }
                            Switch(
                                checked = subscribeEnabled,
                                onCheckedChange = { subscribeEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = Teal600,
                                    checkedThumbColor = White,
                                ),
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ProductDetailBottomBar(
    price: Int,
    onAddToCart: () -> Unit,
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Ink900,
                )
                Text(
                    text = "Free delivery",
                    style = MaterialTheme.typography.labelSmall,
                    color = Teal600,
                )
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onAddToCart,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Coral,
                    contentColor = White,
                ),
                modifier = Modifier.height(48.dp),
            ) {
                Text(
                    text = "Add to cart",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailPreview() {
    PawcareTheme {
        ProductDetailScreen(
            product = sampleProducts.first(),
            onBack = {},
            onAddToCart = {},
        )
    }
}
