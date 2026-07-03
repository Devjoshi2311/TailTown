package com.tailtown.backend.application.payments

import com.razorpay.RazorpayClient
import com.tailtown.backend.platform.exception.ExternalServiceException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class RazorpayPaymentInfo(
    val id: String,
    val orderId: String?,
    val status: String,
    val amountPaise: Long,
    val currency: String
)

@Component
class RazorpayGatewayClient(
    @Value("\${razorpay.key-id}") private val keyId: String,
    @Value("\${razorpay.key-secret}") private val keySecret: String,
    @Value("\${razorpay.webhook-secret}") private val webhookSecret: String
) {

    val publicKeyId: String get() = keyId

    private val client: RazorpayClient by lazy { RazorpayClient(keyId, keySecret) }

    fun createOrder(amountPaise: Long, currency: String, receipt: String): String {
        try {
            val request = JSONObject()
                .put("amount", amountPaise)
                .put("currency", currency)
                .put("receipt", receipt)
                .put("payment_capture", 1)
            val order = client.orders.create(request)
            return order.toJson().getString("id")
        } catch (e: Exception) {
            throw ExternalServiceException("razorpay", e)
        }
    }

    /** HMAC-SHA256("razorpayOrderId|razorpayPaymentId", key_secret) — the checkout-success signature. */
    fun verifyPaymentSignature(razorpayOrderId: String, razorpayPaymentId: String, signature: String): Boolean =
        safeHmacEquals("$razorpayOrderId|$razorpayPaymentId", keySecret, signature)

    /** HMAC-SHA256(rawRequestBody, webhook_secret) — verified over the exact bytes Razorpay signed. */
    fun verifyWebhookSignature(rawBody: String, signatureHeader: String): Boolean =
        safeHmacEquals(rawBody, webhookSecret, signatureHeader)

    // A misconfigured/blank secret (or a malformed signature header) must fail verification, never throw —
    // an uncaught exception here would surface as an opaque 500 instead of a clean "payment declined".
    private fun safeHmacEquals(data: String, secret: String, signature: String): Boolean =
        try {
            constantTimeEquals(hmacSha256Hex(data, secret), signature)
        } catch (e: Exception) {
            false
        }

    fun fetchPayment(paymentId: String): RazorpayPaymentInfo {
        try {
            return client.payments.fetch(paymentId).toJson().toPaymentInfo()
        } catch (e: Exception) {
            throw ExternalServiceException("razorpay", e)
        }
    }

    fun fetchOrderPayments(razorpayOrderId: String): List<RazorpayPaymentInfo> {
        try {
            return client.orders.fetchPayments(razorpayOrderId).map { it.toJson().toPaymentInfo() }
        } catch (e: Exception) {
            throw ExternalServiceException("razorpay", e)
        }
    }

    private fun JSONObject.toPaymentInfo() = RazorpayPaymentInfo(
        id = getString("id"),
        orderId = optString("order_id", null),
        status = getString("status"),
        amountPaise = getLong("amount"),
        currency = getString("currency")
    )

    private fun hmacSha256Hex(data: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(data.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }

    private fun constantTimeEquals(a: String, b: String): Boolean =
        MessageDigest.isEqual(a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))
}
