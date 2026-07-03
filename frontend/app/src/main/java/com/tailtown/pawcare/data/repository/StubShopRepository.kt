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

    override suspend fun placeOrder(addressId: String): OrderResult {
        val total = computeState().total
        return OrderResult(
            orderId = "stub-order-id",
            orderNumber = "ORD-STUB001",
            status = "PENDING_PAYMENT",
            paymentStatus = "PENDING",
            grandTotal = total,
            currency = "INR",
            razorpayOrderId = "order_stub",
            razorpayKeyId = "rzp_test_stub",
            amountInPaise = total * 100L,
        )
    }

    override suspend fun verifyPayment(
        orderId: String,
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String,
    ): OrderResult {
        val total = computeState().total
        return OrderResult(
            orderId = orderId,
            orderNumber = "ORD-STUB001",
            status = "PLACED",
            paymentStatus = "PAID",
            grandTotal = total,
            currency = "INR",
        )
    }

    override suspend fun getOrder(orderId: String): OrderResult = verifyPayment(orderId, "", "", "")
}
