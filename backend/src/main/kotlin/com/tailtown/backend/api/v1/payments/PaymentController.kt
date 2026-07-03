package com.tailtown.backend.api.v1.payments

import com.tailtown.backend.api.v1.booking.BookingResponse
import com.tailtown.backend.api.v1.orders.OrderResponse
import com.tailtown.backend.application.orders.OrderService
import com.tailtown.backend.application.payments.BookingPaymentService
import com.tailtown.backend.application.payments.PaymentService
import com.tailtown.backend.application.payments.RazorpayGatewayClient
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val orderService: OrderService,
    private val bookingPaymentService: BookingPaymentService,
    private val razorpayGatewayClient: RazorpayGatewayClient
) {

    @PostMapping("/verify")
    fun verify(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: VerifyPaymentRequest
    ): ResponseEntity<OrderResponse> {
        val order = paymentService.verifyAndCapture(
            userId = principal.userId,
            orderId = request.orderId!!,
            razorpayOrderId = request.razorpayOrderId,
            razorpayPaymentId = request.razorpayPaymentId,
            razorpaySignature = request.razorpaySignature
        )
        val items = orderService.getOrderItems(order.id)
        return ResponseEntity.ok(OrderResponse.from(order, items, razorpayGatewayClient.publicKeyId))
    }

    @PostMapping("/verify-booking")
    fun verifyBooking(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: VerifyBookingPaymentRequest
    ): ResponseEntity<BookingResponse> {
        val booking = bookingPaymentService.verifyAndCapture(
            userId = principal.userId,
            bookingId = request.bookingId!!,
            razorpayOrderId = request.razorpayOrderId,
            razorpayPaymentId = request.razorpayPaymentId,
            razorpaySignature = request.razorpaySignature
        )
        return ResponseEntity.ok(BookingResponse.from(booking, razorpayGatewayClient.publicKeyId))
    }

    // No JWT here (see SecurityConfig) — the X-Razorpay-Signature HMAC check is this endpoint's authentication.
    // Body is read as a raw String so the signature is verified over the exact bytes Razorpay signed.
    @PostMapping("/webhook")
    fun webhook(
        @RequestBody rawBody: String,
        @RequestHeader("X-Razorpay-Signature") signature: String
    ): ResponseEntity<Void> {
        paymentService.handleWebhook(rawBody, signature)
        return ResponseEntity.ok().build()
    }
}
