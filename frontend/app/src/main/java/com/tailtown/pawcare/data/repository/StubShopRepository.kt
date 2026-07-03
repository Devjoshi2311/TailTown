package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.shop.CartItem
import com.tailtown.pawcare.ui.shop.sampleCartItems

class StubShopRepository : ShopRepository {
    private val items = sampleCartItems.toMutableList()

    private fun computeState(): CartState {
        val subtotal = items.sumOf { it.unitPrice * it.qty }
        val saving = items.filter { it.variantLabel.contains("Auto") }.sumOf { it.unitPrice * it.qty / 10 }
        return CartState(items.toList(), subtotal, saving, subtotal - saving)
    }

    override suspend fun getCart() = computeState()

    override suspend fun addItem(item: CartItem): CartState {
        val existing = items.indexOfFirst { it.product.id == item.product.id }
        if (existing >= 0) items[existing] = items[existing].copy(qty = items[existing].qty + 1)
        else items.add(item)
        return computeState()
    }

    override suspend fun updateQty(productId: String, delta: Int): CartState {
        val idx = items.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            val newQty = items[idx].qty + delta
            if (newQty <= 0) items.removeAt(idx) else items[idx] = items[idx].copy(qty = newQty)
        }
        return computeState()
    }

    override suspend fun removeItem(productId: String): CartState {
        items.removeIf { it.product.id == productId }
        return computeState()
    }
}
