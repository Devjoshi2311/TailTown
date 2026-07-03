package com.tailtown.pawcare.ui.shop

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.BoneWarm
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.White

private data class MallNavItem(val label: String, val icon: ImageVector)

private val mallNavItems = listOf(
    MallNavItem("Explore", Icons.Default.Search),
    MallNavItem("Inbox", Icons.Default.Mail),
    MallNavItem("Visits", Icons.Default.CalendarToday),
    MallNavItem("Shop", Icons.Default.ShoppingBag),
    MallNavItem("Me", Icons.Default.Person),
)

@Composable
fun MallHomeScreen(
    products: List<ShopProduct>,
    promotion: Promotion? = null,
    petName: String = "your pet",
    unreadCount: Int = 0,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onSeeAllPicks: () -> Unit,
    onCartClick: () -> Unit,
    onNavigateHome: () -> Unit,
    onInbox: () -> Unit = {},
    onVisits: () -> Unit,
    onPetProfile: () -> Unit,
) {
    var selectedNav by remember { mutableIntStateOf(3) }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── White header ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Pet mall",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900,
            )
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Coral),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$unreadCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = White,
                    )
                }
            }
        }

        // ── Scrollable content (Bone background) ─────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Bone)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Offer banner ─────────────────────────────────────────────────
            if (promotion != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BoneWarm)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Text(
                        text = promotion.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = Coral,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = promotion.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Ink900,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "${promotion.endsAt} · ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink500,
                        )
                        Text(
                            text = promotion.ctaLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Coral,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Shop by category ─────────────────────────────────────────────
            Text(
                text = "Shop by category",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))

            // Row 1: first 4 categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                shopCategories.take(4).forEach { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Row 2: next 4 categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                shopCategories.drop(4).take(4).forEach { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category.id) },
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Bruno picks ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "$petName picks",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = Ink900,
                    modifier = Modifier.clickable { onSeeAllPicks() },
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(products.take(4)) { product ->
                    MiniProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // ── Bottom Navigation ─────────────────────────────────────────────────
        NavigationBar(
            containerColor = White,
            tonalElevation = 0.dp,
            modifier = Modifier.navigationBarsPadding(),
        ) {
            mallNavItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedNav,
                    onClick = {
                        selectedNav = index
                        when (index) {
                            0 -> onNavigateHome()
                            1 -> onInbox()
                            2 -> onVisits()
                            4 -> onPetProfile()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Coral,
                        selectedTextColor = Coral,
                        unselectedIconColor = Ink500,
                        unselectedTextColor = Ink500,
                        indicatorColor = CoralSoft,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(category: ShopCategory, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(category.tint),
            contentAlignment = Alignment.Center,
        ) {
            if (category.emoji.isNotEmpty()) {
                Text(
                    text = category.emoji,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink900,
        )
    }
}

@Composable
private fun MiniProductCard(product: ShopProduct, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() },
    ) {
        // Hero box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
        Column(modifier = Modifier.padding(10.dp)) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${product.price}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                if (product.originalPrice != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "₹${product.originalPrice}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            textDecoration = TextDecoration.LineThrough,
                        ),
                        color = Ink500,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MallHomeScreenPreview() {
    PawcareTheme {
        MallHomeScreen(
            products = sampleProducts,
            onCategoryClick = {},
            onProductClick = {},
            onSeeAllPicks = {},
            onCartClick = {},
            onNavigateHome = {},
            onVisits = {},
            onPetProfile = {},
        )
    }
}
