package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Product ───────────────────────────────────────────────────────────────────

@Serializable
data class ProductVariantDto(
    val id: String,
    val label: String,
    val price: Int,
    @SerialName("subscribe_price") val subscribePrice: Int,
    val stock: Int = 0,
)

@Serializable
data class ShopProductDto(
    val id: String,
    val name: String,
    val description: String = "",
    val category: String,
    val subtitle: String,
    val rating: Float,
    @SerialName("review_count") val reviewCount: Int,
    val price: Int,
    @SerialName("original_price") val originalPrice: Int? = null,
    @SerialName("discount_pct") val discountPct: Int? = null,
    @SerialName("is_bestseller") val isBestseller: Boolean = false,
    val variants: List<ProductVariantDto> = emptyList(),
    @SerialName("photo_urls") val photoUrls: List<String> = emptyList(),
)

/** ApiResponse<ShopProductListData> — GET /shop/products */
@Serializable
data class ShopProductListData(val products: List<ShopProductDto>)

/** ApiResponse<ShopProductData> — GET /shop/products/{id} */
@Serializable
data class ShopProductData(val product: ShopProductDto)

// ── Cart ──────────────────────────────────────────────────────────────────────

@Serializable
data class CartItemDto(
    @SerialName("product_id") val productId: String,
    val product: ShopProductDto,
    @SerialName("variant_id") val variantId: String,
    @SerialName("variant_label") val variantLabel: String,
    val qty: Int,
    @SerialName("unit_price") val unitPrice: Int,
    @SerialName("subscribed_monthly") val subscribedMonthly: Boolean = false,
)

@Serializable
data class CartDto(
    val items: List<CartItemDto>,
    val subtotal: Int,
    @SerialName("delivery_fee") val deliveryFee: Int,
    @SerialName("subscription_saving") val subscriptionSaving: Int,
    val total: Int,
)

/** ApiResponse<CartData> — GET /shop/cart */
@Serializable
data class CartData(val cart: CartDto)

/** POST /shop/cart/items */
@Serializable
data class AddToCartRequest(
    @SerialName("product_id") val productId: String,
    @SerialName("variant_id") val variantId: String,
    val qty: Int,
    @SerialName("subscribe_monthly") val subscribeMonthly: Boolean = false,
)

/** PATCH /shop/cart/items/{id} */
@Serializable
data class UpdateCartItemRequest(val qty: Int)

// ── Order ─────────────────────────────────────────────────────────────────────

@Serializable
enum class ShopOrderStatus {
    @SerialName("confirmed")        CONFIRMED,
    @SerialName("packing")          PACKING,
    @SerialName("out_for_delivery") OUT_FOR_DELIVERY,
    @SerialName("delivered")        DELIVERED,
    @SerialName("cancelled")        CANCELLED,
}

/** POST /shop/orders */
@Serializable
data class PlaceOrderRequest(
    @SerialName("address_id") val addressId: String,
    @SerialName("delivery_slot_id") val deliverySlotId: String,
    @SerialName("payment_method") val paymentMethod: PaymentMethod,   // reuse existing enum from BookingModels
    @SerialName("promo_code") val promoCode: String? = null,
)

@Serializable
data class ShopOrderItemDto(
    @SerialName("product_id") val productId: String,
    val product: ShopProductDto,
    @SerialName("variant_label") val variantLabel: String,
    val qty: Int,
    @SerialName("unit_price") val unitPrice: Int,
)

@Serializable
data class ShopOrderDto(
    val id: String,
    @SerialName("order_number") val orderNumber: String,
    val items: List<ShopOrderItemDto>,
    val status: ShopOrderStatus,
    val subtotal: Int,
    @SerialName("delivery_fee") val deliveryFee: Int,
    @SerialName("subscription_saving") val subscriptionSaving: Int,
    val total: Int,
    @SerialName("estimated_delivery") val estimatedDelivery: String,
    @SerialName("created_at") val createdAt: String,
)

/** ApiResponse<ShopOrderData> — GET /shop/orders/{id} · POST /shop/orders */
@Serializable
data class ShopOrderData(val order: ShopOrderDto)
