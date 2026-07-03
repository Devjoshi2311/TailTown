package com.tailtown.pawcare.data.remote

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.data.remote.dto.AddToCartRequestDto
import com.tailtown.pawcare.data.remote.dto.CartItemResponseDto
import com.tailtown.pawcare.data.remote.dto.UpdateCartItemRequestDto
import com.tailtown.pawcare.data.repository.CartState
import com.tailtown.pawcare.data.repository.ShopRepository
import com.tailtown.pawcare.ui.shop.CartItem
import com.tailtown.pawcare.ui.shop.ShopProduct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteShopRepository @Inject constructor(private val api: ApiService) : ShopRepository {

    // productId → cartItemId (needed because the interface uses productId, but REST uses itemId)
    private val itemIdByProductId = mutableMapOf<String, String>()

    override suspend fun getCart(): CartState {
        return try {
            val cart = api.getCart().data ?: return CartState()
            itemIdByProductId.clear()
            cart.items.forEach { itemIdByProductId[it.product.id] = it.id }
            cart.items.toCartState(cart.subtotal, cart.total)
        } catch (_: Exception) { CartState() }
    }

    override suspend fun addItem(item: CartItem): CartState {
        return try {
            api.addToCart(AddToCartRequestDto(productId = item.product.id, quantity = item.qty))
            getCart()
        } catch (_: Exception) { getCart() }
    }

    override suspend fun updateQty(productId: String, delta: Int): CartState {
        return try {
            val itemId = itemIdByProductId[productId] ?: return getCart()
            val currentQty = api.getCart().data?.items
                ?.find { it.product.id == productId }?.quantity ?: 1
            val newQty = currentQty + delta
            if (newQty <= 0) api.removeFromCart(itemId)
            else api.updateCartItem(itemId, UpdateCartItemRequestDto(quantity = newQty))
            getCart()
        } catch (_: Exception) { getCart() }
    }

    override suspend fun removeItem(productId: String): CartState {
        return try {
            val itemId = itemIdByProductId[productId] ?: return getCart()
            api.removeFromCart(itemId)
            getCart()
        } catch (_: Exception) { getCart() }
    }

    private fun List<CartItemResponseDto>.toCartState(subtotal: Double, total: Double): CartState =
        CartState(
            items = map { dto ->
                CartItem(
                    product = ShopProduct(
                        id = dto.product.id,
                        name = dto.product.name,
                        subtitle = dto.product.description.take(40),
                        rating = 4.5f,
                        reviewCount = 0,
                        price = dto.product.price.toInt(),
                        originalPrice = dto.product.mrp.toInt(),
                        heroTint = Color(0xFFF5F2EC),
                    ),
                    variantLabel = "Standard",
                    qty = dto.quantity,
                    unitPrice = dto.product.price.toInt(),
                )
            },
            subtotal = subtotal.toInt(),
            subscriptionSaving = 0,
            total = total.toInt(),
        )
}
