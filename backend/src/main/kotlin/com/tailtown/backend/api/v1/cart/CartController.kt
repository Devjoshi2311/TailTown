package com.tailtown.backend.api.v1.cart

import com.tailtown.backend.application.cart.CartService
import com.tailtown.backend.infrastructure.persistence.cart.CartEntity
import com.tailtown.backend.infrastructure.persistence.cart.CartItemEntity
import com.tailtown.backend.infrastructure.persistence.shop.ProductRepository
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/api/v1/cart")
class CartController(
    private val cartService: CartService,
    private val productRepository: ProductRepository
) {

    @GetMapping
    fun getCart(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<CartResponse> {
        val (cart, items) = cartService.getOrCreateCart(principal.userId)
        return ResponseEntity.ok(buildCartResponse(cart, items))
    }

    @PostMapping("/items")
    fun addItem(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: AddCartItemRequest
    ): ResponseEntity<CartResponse> {
        val (cart, items) = cartService.addItem(
            principal.userId,
            request.productId!!,
            request.quantity
        )
        return ResponseEntity.ok(buildCartResponse(cart, items))
    }

    @PatchMapping("/items/{cartItemId}")
    fun updateItem(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable cartItemId: UUID,
        @Valid @RequestBody request: UpdateCartItemRequest
    ): ResponseEntity<CartResponse> {
        val (cart, items) = cartService.updateItem(
            principal.userId,
            cartItemId,
            request.quantity,
            request.version
        )
        return ResponseEntity.ok(buildCartResponse(cart, items))
    }

    @DeleteMapping("/items/{cartItemId}")
    fun removeItem(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable cartItemId: UUID
    ): ResponseEntity<CartResponse> {
        val (cart, items) = cartService.removeItem(principal.userId, cartItemId)
        return ResponseEntity.ok(buildCartResponse(cart, items))
    }

    private fun buildCartResponse(cart: CartEntity, items: List<CartItemEntity>): CartResponse {
        val productIds = items.map { it.productId }.toSet()
        val productMap = if (productIds.isNotEmpty()) {
            productRepository.findAllById(productIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        val cartItemResponses = items.map { item ->
            val product = productMap[item.productId]
            val price = item.priceSnapshot ?: product?.price ?: BigDecimal.ZERO
            CartItemResponse(
                id = item.id,
                productId = item.productId,
                productName = product?.name ?: "Unknown Product",
                imageUrl = product?.imageUrl,
                price = price,
                quantity = item.quantity,
                lineTotal = price.multiply(BigDecimal(item.quantity)),
                version = item.version
            )
        }

        val subtotal = cartItemResponses.fold(BigDecimal.ZERO) { acc, i -> acc.add(i.lineTotal) }
        val deliveryFee = BigDecimal.ZERO
        val discountTotal = BigDecimal.ZERO
        val taxTotal = BigDecimal.ZERO
        val total = subtotal.add(deliveryFee).subtract(discountTotal).add(taxTotal)

        return CartResponse(
            id = cart.id,
            items = cartItemResponses,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discountTotal = discountTotal,
            taxTotal = taxTotal,
            total = total,
            currency = cart.currency,
            version = cart.version
        )
    }
}
