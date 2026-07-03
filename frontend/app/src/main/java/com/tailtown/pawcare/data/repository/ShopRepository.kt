package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.shop.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val subscriptionSaving: Int = 0,
    val total: Int = 0,
)

data class OrderResult(
    val orderId: String,
    val orderNumber: String,
    val status: String,
    val paymentStatus: String,
    val grandTotal: Int,
    val currency: String,
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val amountInPaise: Long? = null,
)

interface ShopRepository {
    suspend fun getCart(): CartState
    suspend fun addItem(item: CartItem): CartState
    suspend fun updateQty(productId: String, delta: Int): CartState
    suspend fun removeItem(productId: String): CartState

    // Unlike the cart methods above, these intentionally let exceptions propagate — the checkout
    // flow needs to tell "backend unreachable" apart from "backend rejected it", not silently fall back.
    suspend fun placeOrder(addressId: String): OrderResult
    suspend fun verifyPayment(
        orderId: String,
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String,
    ): OrderResult
    suspend fun getOrder(orderId: String): OrderResult
}
