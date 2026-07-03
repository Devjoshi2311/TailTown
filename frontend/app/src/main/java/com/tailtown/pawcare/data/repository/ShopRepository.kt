package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.shop.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Int = 0,
    val subscriptionSaving: Int = 0,
    val total: Int = 0,
)

interface ShopRepository {
    suspend fun getCart(): CartState
    suspend fun addItem(item: CartItem): CartState
    suspend fun updateQty(productId: String, delta: Int): CartState
    suspend fun removeItem(productId: String): CartState
}
