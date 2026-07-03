package com.tailtown.pawcare.ui.home

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tailtown.pawcare.common.UiState
import com.tailtown.pawcare.ui.shop.ShopProduct
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White
import com.tailtown.pawcare.ui.vet.Vet

private val homeTabs = listOf("Vets", "Food", "Toys", "Groom")

private data class HomeNavItem(val label: String, val icon: ImageVector)

private val homeNavItems = listOf(
    HomeNavItem("Explore", Icons.Default.Search),
    HomeNavItem("Inbox", Icons.Default.Mail),
    HomeNavItem("Visits", Icons.Default.CalendarToday),
    HomeNavItem("Shop", Icons.Default.ShoppingBag),
    HomeNavItem("Me", Icons.Default.Person),
)

@Composable
fun HomeScreen(
    vets: UiState<List<Vet>>,
    petName: String = "your pet",
    foodProducts: List<ShopProduct> = emptyList(),
    toyProducts: List<ShopProduct> = emptyList(),
    groomers: List<Vet> = emptyList(),
    onVetClick: (String) -> Unit,
    onShowAllVets: () -> Unit,
    onProductClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onInbox: () -> Unit = {},
    onVisits: () -> Unit,
    onShop: () -> Unit = {},
    onPetProfile: () -> Unit,
    onRetryVets: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedNav by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Bone,
        bottomBar = {
            NavigationBar(
                containerColor = White,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                homeNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedNav,
                        onClick = {
                            selectedNav = index
                            when (index) {
                                1 -> onInbox()
                                2 -> onVisits()
                                3 -> onShop()
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
                            Text(text = item.label, style = MaterialTheme.typography.labelSmall)
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
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Bone)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Find care for $petName",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Ink900,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Vets · Food · Toys · Grooming",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink500,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(CoralSoft),
                    )
                }
                Spacer(Modifier.height(4.dp))
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = White,
                    contentColor = Ink900,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Ink900),
                        )
                    },
                    divider = { HorizontalDivider(color = Hairline) },
                ) {
                    homeTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTab,
                            onClick = {
                                selectedTab = index
                                onTabSelected(index)
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (index == selectedTab) FontWeight.Medium else FontWeight.Normal,
                                    ),
                                    color = if (index == selectedTab) Ink900 else Ink500,
                                )
                            },
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> VetsContent(vets = vets, onVetClick = onVetClick, onShowAllVets = onShowAllVets, onRetry = onRetryVets)
                1 -> ProductContent(title = "Food", products = foodProducts, onProductClick = onProductClick, onShowAll = onShop)
                2 -> ProductContent(title = "Toys", products = toyProducts, onProductClick = onProductClick, onShowAll = onShop)
                3 -> GroomContent(groomers = groomers)
            }
        }
    }
}

@Composable
private fun VetsContent(
    vets: UiState<List<Vet>>,
    onVetClick: (String) -> Unit,
    onShowAllVets: () -> Unit,
    onRetry: () -> Unit,
) {
    when (vets) {
        is UiState.Loading -> EmptyTabContent()
        is UiState.Error -> ErrorTabContent(message = vets.message, onRetry = onRetry)
        is UiState.Success -> {
            if (vets.data.isEmpty()) {
                EmptyMessage(message = "No vets available right now.")
                return
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { SectionHeader(title = "Top vets near you", onShowAll = onShowAllVets) }
                items(vets.data, key = { it.id }) { vet ->
                    HomeVetCard(vet = vet, onClick = { onVetClick(vet.id) })
                }
            }
        }
    }
}

@Composable
private fun ErrorTabContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Ink500)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium,
            ),
            color = Ink900,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}

@Composable
private fun EmptyMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Ink500)
    }
}

@Composable
private fun ProductContent(
    title: String,
    products: List<ShopProduct>,
    onProductClick: (String) -> Unit,
    onShowAll: () -> Unit,
) {
    if (products.isEmpty()) {
        EmptyTabContent()
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { SectionHeader(title = title, onShowAll = onShowAll) }
        items(products, key = { it.id }) { product ->
            HomeProductCard(product = product, onClick = { onProductClick(product.id) })
        }
    }
}

@Composable
private fun GroomContent(groomers: List<Vet>) {
    if (groomers.isEmpty()) {
        EmptyTabContent()
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Groomers near you",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink900,
            )
        }
        items(groomers, key = { it.id }) { groomer ->
            HomeVetCard(vet = groomer, onClick = {})
        }
    }
}

@Composable
private fun EmptyTabContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Coral, strokeWidth = 2.dp)
    }
}

@Composable
private fun SectionHeader(title: String, onShowAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Ink900,
        )
        Text(
            text = "Show all",
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium,
            ),
            color = Ink900,
            modifier = Modifier.clickable { onShowAll() },
        )
    }
}

@Composable
private fun HomeVetCard(vet: Vet, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).background(vet.heroTint),
            ) {
                if (vet.imageUrl != null) {
                    AsyncImage(
                        model = vet.imageUrl,
                        contentDescription = vet.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                if (vet.isSuperhost) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Hairline, PillShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(text = "Superhost vet", style = MaterialTheme.typography.labelSmall, color = Ink900)
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
                    StarRating(rating = vet.rating)
                }
                Spacer(Modifier.height(4.dp))
                Text(text = "${vet.specialty} · ${vet.location}", style = MaterialTheme.typography.bodyMedium, color = Ink500)
                if (vet.pricePerVisit > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(text = "₹${vet.pricePerVisit} / visit", style = MaterialTheme.typography.bodyMedium, color = Ink500)
                }
            }
        }
    }
}

@Composable
private fun HomeProductCard(product: ShopProduct, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row {
            Box(modifier = Modifier.size(110.dp).background(product.heroTint)) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                if (product.isBestseller) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Coral, PillShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(text = "Bestseller", style = MaterialTheme.typography.labelSmall, color = White)
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f).padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                if (product.subtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(text = product.subtitle, style = MaterialTheme.typography.bodyMedium, color = Ink500)
                }
                Spacer(Modifier.height(6.dp))
                StarRating(rating = product.rating, reviews = product.reviewCount)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.price}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Ink900,
                    )
                    if (product.originalPrice != null) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "₹${product.originalPrice}",
                            style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                            color = Ink500,
                        )
                    }
                    if (product.discountPct != null) {
                        Spacer(Modifier.width(6.dp))
                        Text(text = "${product.discountPct}% off", style = MaterialTheme.typography.labelSmall, color = Coral)
                    }
                }
            }
        }
    }
}

@Composable
private fun StarRating(rating: Float, reviews: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Coral, modifier = Modifier.size(13.dp))
        Text(
            text = if (reviews != null) "$rating · $reviews" else "$rating",
            style = MaterialTheme.typography.labelSmall,
            color = Ink900,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    PawcareTheme {
        HomeScreen(vets = UiState.Success(emptyList()), onVetClick = {}, onShowAllVets = {}, onVisits = {}, onPetProfile = {})
    }
}
