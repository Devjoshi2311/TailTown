package com.tailtown.backend.application.cart

import com.tailtown.backend.infrastructure.persistence.cart.CartEntity
import com.tailtown.backend.infrastructure.persistence.cart.CartItemEntity
import com.tailtown.backend.infrastructure.persistence.cart.CartItemRepository
import com.tailtown.backend.infrastructure.persistence.cart.CartRepository
import com.tailtown.backend.infrastructure.persistence.shop.ProductRepository
import com.tailtown.backend.platform.exception.ForbiddenException
import com.tailtown.backend.platform.exception.OutOfStockException
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository
) {

    private fun findOrCreateActiveCart(userId: UUID): CartEntity {
        return cartRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, "ACTIVE")
            ?: cartRepository.save(CartEntity(userId = userId))
    }

    @Transactional(readOnly = true)
    fun getOrCreateCart(userId: UUID): Pair<CartEntity, List<CartItemEntity>> {
        val cart = cartRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, "ACTIVE")
            ?: return Pair(cartRepository.save(CartEntity(userId = userId)), emptyList())
        val items = cartItemRepository.findAllByCartIdAndDeletedAtIsNull(cart.id)
        return Pair(cart, items)
    }

    fun addItem(userId: UUID, productId: UUID, quantity: Int): Pair<CartEntity, List<CartItemEntity>> {
        val cart = findOrCreateActiveCart(userId)

        val product = productRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(productId)
            ?: throw ResourceNotFoundException("Product", productId)

        if (product.stockQty < 1) {
            throw OutOfStockException(productId)
        }

        val existingItem = cartItemRepository.findByCartIdAndProductIdAndDeletedAtIsNull(cart.id, productId)
        if (existingItem != null) {
            existingItem.quantity = existingItem.quantity + quantity
            existingItem.priceSnapshot = product.price
            cartItemRepository.save(existingItem)
        } else {
            val newItem = CartItemEntity(
                cartId = cart.id,
                productId = productId,
                quantity = quantity,
                priceSnapshot = product.price,
                currency = product.currency
            )
            cartItemRepository.save(newItem)
        }

        val items = cartItemRepository.findAllByCartIdAndDeletedAtIsNull(cart.id)
        return Pair(cart, items)
    }

    fun updateItem(
        userId: UUID,
        cartItemId: UUID,
        quantity: Int,
        version: Long
    ): Pair<CartEntity, List<CartItemEntity>> {
        val item = cartItemRepository.findById(cartItemId).orElse(null)
            ?: throw ResourceNotFoundException("CartItem", cartItemId)

        val cart = cartRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, "ACTIVE")
            ?: throw ResourceNotFoundException("Cart", userId)

        if (item.cartId != cart.id) {
            throw ForbiddenException("Cart item does not belong to the user's active cart")
        }

        if (item.version != version) {
            throw VersionConflictException()
        }

        if (quantity == 0) {
            item.softDelete()
            cartItemRepository.save(item)
        } else {
            item.quantity = quantity
            cartItemRepository.save(item)
        }

        val items = cartItemRepository.findAllByCartIdAndDeletedAtIsNull(cart.id)
        return Pair(cart, items)
    }

    fun removeItem(userId: UUID, cartItemId: UUID): Pair<CartEntity, List<CartItemEntity>> {
        val item = cartItemRepository.findById(cartItemId).orElse(null)
            ?: throw ResourceNotFoundException("CartItem", cartItemId)

        val cart = cartRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, "ACTIVE")
            ?: throw ResourceNotFoundException("Cart", userId)

        if (item.cartId != cart.id) {
            throw ForbiddenException("Cart item does not belong to the user's active cart")
        }

        item.softDelete()
        cartItemRepository.save(item)

        val items = cartItemRepository.findAllByCartIdAndDeletedAtIsNull(cart.id)
        return Pair(cart, items)
    }
}
