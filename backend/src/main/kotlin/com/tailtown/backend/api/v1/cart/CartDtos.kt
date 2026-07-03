package com.tailtown.backend.api.v1.cart

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class AddCartItemRequest(
    @field:NotNull(message = "productId is required")
    val productId: UUID?,

    @field:Min(value = 1, message = "quantity must be at least 1")
    @field:Max(value = 99, message = "quantity must not exceed 99")
    val quantity: Int = 1
)

data class UpdateCartItemRequest(
    @field:Min(value = 0, message = "quantity must be at least 0")
    @field:Max(value = 99, message = "quantity must not exceed 99")
    val quantity: Int,

    val version: Long
)

data class CartItemResponse(
    val id: UUID,
    val productId: UUID,
    val productName: String,
    val imageUrl: String?,
    val price: BigDecimal,
    val quantity: Int,
    val lineTotal: BigDecimal,
    val version: Long
)

data class CartResponse(
    val id: UUID,
    val items: List<CartItemResponse>,
    val subtotal: BigDecimal,
    val deliveryFee: BigDecimal,
    val discountTotal: BigDecimal,
    val taxTotal: BigDecimal,
    val total: BigDecimal,
    val currency: String,
    val version: Long
)
