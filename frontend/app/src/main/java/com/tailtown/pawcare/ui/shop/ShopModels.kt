package com.tailtown.pawcare.ui.shop

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.ui.theme.CoralSoft

// ── UI models ─────────────────────────────────────────────────────────────────

data class ShopCategory(val id: String, val label: String, val tint: Color, val emoji: String = "")

data class ShopProduct(
    val id: String,
    val name: String,
    val subtitle: String,           // e.g. "3kg · Adult"
    val rating: Float,
    val reviewCount: Int,
    val price: Int,
    val originalPrice: Int? = null,
    val discountPct: Int? = null,
    val heroTint: Color,
    val isBestseller: Boolean = false,
    val description: String = "",
    val imageUrl: String? = null,
)

data class PackVariant(val label: String, val price: Int, val subscribePrice: Int)

data class CartItem(
    val product: ShopProduct,
    val variantLabel: String,
    val qty: Int,
    val unitPrice: Int,
)

enum class StepStatus { DONE, ACTIVE, PENDING }

data class DeliveryStep(val label: String, val timeLabel: String, val status: StepStatus)

// ── Sample data ───────────────────────────────────────────────────────────────

val shopCategories = listOf(
    ShopCategory(id = "food",    label = "Food",    tint = CoralSoft,          emoji = "🦴"),
    ShopCategory(id = "toys",    label = "Toys",    tint = Color(0xFFD6EFE8),  emoji = "🎾"),
    ShopCategory(id = "apparel", label = "Apparel", tint = Color(0xFFDDE5F4),  emoji = "👕"),
    ShopCategory(id = "meds",    label = "Meds",    tint = Color(0xFFDDE5F4),  emoji = "💊"),
    ShopCategory(id = "bath",    label = "Bath",    tint = CoralSoft,          emoji = "🛁"),
    ShopCategory(id = "bedding", label = "Bedding", tint = Color(0xFFF5EACF),  emoji = "🛏"),
    ShopCategory(id = "litter",  label = "Litter",  tint = CoralSoft,          emoji = "🪣"),
    ShopCategory(id = "all",     label = "All",     tint = Color(0xFFEFEFEF),  emoji = "🛍"),
)

val sampleProducts = listOf(
    ShopProduct(
        id            = "royal-chow",
        name          = "Royal chow",
        subtitle      = "3kg · Adult",
        rating        = 4.6f,
        reviewCount   = 1200,
        price         = 1299,
        originalPrice = 1599,
        discountPct   = 19,
        heroTint      = CoralSoft,
        isBestseller  = true,
        description   = "Complete nutrition for adult Labradors",
    ),
    ShopProduct(
        id          = "salmon-pate",
        name        = "Salmon paté",
        subtitle    = "85g · Cat",
        rating      = 4.8f,
        reviewCount = 540,
        price       = 89,
        heroTint    = Color(0xFFD6EFE8),
    ),
    ShopProduct(
        id          = "jerky-treats",
        name        = "Jerky treats",
        subtitle    = "200g · All breeds",
        rating      = 4.7f,
        reviewCount = 320,
        price       = 399,
        heroTint    = Color(0xFFF5EACF),
    ),
    ShopProduct(
        id          = "puppy-starter",
        name        = "Puppy starter",
        subtitle    = "1kg · Pup",
        rating      = 4.5f,
        reviewCount = 188,
        price       = 629,
        heroTint    = Color(0xFFDDE5F4),
    ),
)

val chewRope = ShopProduct(
    id          = "chew-rope",
    name        = "Chew rope",
    subtitle    = "Medium · Cotton",
    rating      = 4.5f,
    reviewCount = 98,
    price       = 249,
    heroTint    = Color(0xFFD6EFE8),
)

val brunoPicks = listOf(sampleProducts[0], chewRope)

val royalChowVariants = listOf(
    PackVariant(label = "1kg",  price = 699,  subscribePrice = 629),
    PackVariant(label = "3kg",  price = 1299, subscribePrice = 1169),
    PackVariant(label = "10kg", price = 3299, subscribePrice = 2969),
)

val sampleCartItems = listOf(
    CartItem(
        product      = sampleProducts[0].copy(name = "Royal chow adult"),
        variantLabel = "3kg · Auto-monthly",
        qty          = 1,
        unitPrice    = 1169,
    ),
    CartItem(
        product      = chewRope,
        variantLabel = "Medium · Cotton",
        qty          = 2,
        unitPrice    = 249,
    ),
)

val sampleTrackingSteps = listOf(
    DeliveryStep(label = "Order confirmed",   timeLabel = "Just now",            status = StepStatus.DONE),
    DeliveryStep(label = "Packing your order", timeLabel = "Today by 6 PM",      status = StepStatus.ACTIVE),
    DeliveryStep(label = "Out for delivery",  timeLabel = "Tomorrow morning",    status = StepStatus.PENDING),
    DeliveryStep(label = "Delivered",         timeLabel = "Tomorrow 10 AM–1 PM", status = StepStatus.PENDING),
)
