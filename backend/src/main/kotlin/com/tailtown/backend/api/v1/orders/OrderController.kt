package com.tailtown.backend.api.v1.orders

import com.tailtown.backend.application.orders.OrderService
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateOrderRequest,
        @RequestHeader(name = "X-Idempotency-Key", required = false) idempotencyKey: String?
    ): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(
            userId = principal.userId,
            addressId = request.addressId!!,
            idempotencyKey = idempotencyKey
        )
        val items = orderService.getOrderItems(order.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order, items))
    }

    @GetMapping
    fun listOrders(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<OrderResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "placedAt"))
        val ordersPage = orderService.listOrders(principal.userId, pageable)
        val orderResponses = ordersPage.content.map { order ->
            val items = orderService.getOrderItems(order.id)
            OrderResponse.from(order, items)
        }
        return ResponseEntity.ok(orderResponses)
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable orderId: UUID
    ): ResponseEntity<OrderResponse> {
        val order = orderService.getOrder(principal.userId, orderId)
        val items = orderService.getOrderItems(order.id)
        return ResponseEntity.ok(OrderResponse.from(order, items))
    }
}
