package com.tailtown.pawcare.ui.account

// ── Address ───────────────────────────────────────────────────────────────────

data class Address(
    val id: String,
    val label: String,
    val street: String,
    val city: String,
    val pincode: String,
    val isDefault: Boolean = false,
)

val sampleAddresses = listOf(
    Address("a1", "Home", "12, Koramangala 4th Block", "Bengaluru", "560034", isDefault = true),
    Address("a2", "Work", "91 Springboard, HSR Layout", "Bengaluru", "560102"),
)

// ── Payment method ────────────────────────────────────────────────────────────

enum class PaymentMethodType { UPI, CARD, NETBANKING }

data class SavedPaymentMethod(
    val id: String,
    val type: PaymentMethodType,
    val label: String,
    val masked: String,
    val isDefault: Boolean = false,
)

val samplePaymentMethods = listOf(
    SavedPaymentMethod("p1", PaymentMethodType.UPI,  "Google Pay",  "riya@oksbi",    isDefault = true),
    SavedPaymentMethod("p2", PaymentMethodType.CARD, "HDFC Visa",   "•••• 4321"),
    SavedPaymentMethod("p3", PaymentMethodType.CARD, "ICICI Master","•••• 8876"),
)

// ── Order history ─────────────────────────────────────────────────────────────

enum class OrderStatus { DELIVERED, IN_TRANSIT, CANCELLED, PROCESSING }

data class OrderSummary(
    val id: String,
    val dateLabel: String,
    val itemsLabel: String,
    val total: Int,
    val status: OrderStatus,
)

val sampleOrders = listOf(
    OrderSummary("ORD-1091", "12 Jun 2026", "Royal Chow 3kg, Chew Rope",   1537, OrderStatus.DELIVERED),
    OrderSummary("ORD-1045", "28 May 2026", "Pedigree Adult 5kg",           899,  OrderStatus.DELIVERED),
    OrderSummary("ORD-0987", "10 May 2026", "Frontline Spot-On, Shampoo",   640,  OrderStatus.DELIVERED),
    OrderSummary("ORD-0912", "22 Apr 2026", "Royal Chow 3kg",              1299,  OrderStatus.CANCELLED),
)

// ── Subscriptions ─────────────────────────────────────────────────────────────

data class SubscriptionItem(
    val id: String,
    val productName: String,
    val variantLabel: String,
    val nextDelivery: String,
    val pricePerCycle: Int,
    val isActive: Boolean = true,
)

val sampleSubscriptions = listOf(
    SubscriptionItem("s1", "Royal Chow",  "3kg · Monthly",  "15 Jul 2026", 1169),
    SubscriptionItem("s2", "Pedigree Pro","5kg · Monthly",  "18 Jul 2026",  809),
    SubscriptionItem("s3", "Dental Chews","Pack of 10 · Monthly", "20 Jul 2026", 299, isActive = false),
)

// ── Referral ──────────────────────────────────────────────────────────────────

data class ReferralInfo(
    val code: String,
    val referrerReward: Int,
    val refereeReward: Int,
    val referralsMade: Int,
    val rewardsEarned: Int,
)

val sampleReferral = ReferralInfo(
    code = "RIYA200",
    referrerReward = 200,
    refereeReward = 100,
    referralsMade = 3,
    rewardsEarned = 600,
)

// ── Help & support ────────────────────────────────────────────────────────────

data class FaqItem(val question: String, val answer: String)

val sampleFaqs = listOf(
    FaqItem(
        "How do I reschedule a vet appointment?",
        "Go to Visits → tap the booking → Reschedule. Changes must be made at least 2 hours before the slot."
    ),
    FaqItem(
        "Can I cancel a subscription?",
        "Yes. Go to Account → Subscriptions → tap the plan → Cancel subscription. No cancellation fee."
    ),
    FaqItem(
        "When will my order arrive?",
        "Standard delivery is 2–4 working days. Express same-day delivery is available in select cities."
    ),
    FaqItem(
        "How does the referral reward work?",
        "Share your code. When a friend places their first order you both get the reward credited to your wallet."
    ),
    FaqItem(
        "What vaccinations are tracked automatically?",
        "Core vaccines (Rabies, DHPP, Bordetella) are pre-loaded based on breed. You can add custom records."
    ),
)

// ── Settings ──────────────────────────────────────────────────────────────────

data class NotificationPrefs(
    val appointments: Boolean = true,
    val medications: Boolean = true,
    val orders: Boolean = true,
    val promotions: Boolean = false,
)

data class AppPrefs(
    val locationEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val language: String = "English",
)
