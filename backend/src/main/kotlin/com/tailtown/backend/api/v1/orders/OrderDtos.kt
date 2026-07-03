package com.tailtown.backend.api.v1.orders

import com.tailtown.backend.infrastructure.persistence.orders.OrderEntity
import com.tailtown.backend.infrastructure.persistence.orders.OrderItemEntity
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CreateOrderRequest(
    @field:NotNull(message = "addressId is required")
    val addressId: UUID?,

    val paymentMethodId: String? = null,

    val notes: String? = null
)

data class OrderItemResponse(
    val productId: UUID?,
    val sku: String,
    val productName: String,
    val productImageUrl: String?,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val lineTotal: BigDecimal
) {
    companion object {
        fun from(entity: OrderItemEntity) = OrderItemResponse(
            productId = entity.productId,
            sku = entity.sku,
            productName = entity.productName,
            productImageUrl = entity.productImageUrl,
            quantity = entity.quantity,
            unitPrice = entity.unitPrice,
            lineTotal = entity.lineTotal
        )
    }
}

data class OrderResponse(
    val id: UUID,
    val orderNumber: String,
    val status: String,
    val paymentStatus: String,
    val items: List<OrderItemResponse>,
    val subtotal: BigDecimal,
    val discountTotal: BigDecimal,
    val deliveryFee: BigDecimal,
    val taxTotal: BigDecimal,
    val grandTotal: BigDecimal,
    val currency: String,
    val deliveryAddressSnapshot: String,
    val placedAt: Instant?,
    val version: Long,
    // Only populated while paymentStatus == "PENDING" — everything the client needs to open Razorpay Checkout.
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val amountInPaise: Long? = null
) {
    companion object {
        fun from(entity: OrderEntity, items: List<OrderItemEntity>, razorpayKeyId: String? = null) = OrderResponse(
            id = entity.id,
            orderNumber = entity.orderNumber,
            status = entity.status,
            paymentStatus = entity.paymentStatus,
            items = items.map { OrderItemResponse.from(it) },
            subtotal = entity.subtotal,
            discountTotal = entity.discountTotal,
            deliveryFee = entity.deliveryFee,
            taxTotal = entity.taxTotal,
            grandTotal = entity.grandTotal,
            currency = entity.currency,
            deliveryAddressSnapshot = entity.deliveryAddressSnapshot,
            placedAt = entity.placedAt,
            version = entity.version,
            razorpayOrderId = entity.razorpayOrderId.takeIf { entity.paymentStatus == "PENDING" },
            razorpayKeyId = razorpayKeyId.takeIf { entity.paymentStatus == "PENDING" },
            amountInPaise = entity.grandTotal.movePointRight(2).longValueExact().takeIf { entity.paymentStatus == "PENDING" }
        )
    }
}
