package com.tailtown.backend.api.v1.booking

import com.tailtown.backend.application.booking.BookingService
import com.tailtown.backend.application.payments.RazorpayGatewayClient
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/bookings")
class BookingController(
    private val bookingService: BookingService,
    private val razorpayGatewayClient: RazorpayGatewayClient
) {

    @GetMapping
    fun listBookings(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<BookingResponse>> {
        val pageable = PageRequest.of(page, size)
        val result = bookingService.listBookings(principal.userId, pageable)
        return ResponseEntity.ok(result.content.map { BookingResponse.from(it, razorpayGatewayClient.publicKeyId) })
    }

    @PostMapping
    fun createBooking(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody @Valid request: CreateBookingRequest
    ): ResponseEntity<BookingResponse> {
        val booking = bookingService.createBooking(
            userId = principal.userId,
            petId = request.petId!!,
            vetId = request.vetId!!,
            slotId = request.slotId!!,
            serviceType = request.serviceType!!,
            visitType = request.visitType!!,
            addressId = request.addressId,
            notes = request.notes
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BookingResponse.from(booking, razorpayGatewayClient.publicKeyId))
    }

    @GetMapping("/{bookingId}")
    fun getBooking(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable bookingId: UUID
    ): ResponseEntity<BookingResponse> {
        val booking = bookingService.getBooking(principal.userId, bookingId)
        return ResponseEntity.ok(BookingResponse.from(booking, razorpayGatewayClient.publicKeyId))
    }

    @PatchMapping("/{bookingId}/cancel")
    fun cancelBooking(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable bookingId: UUID,
        @RequestBody @Valid request: CancelBookingRequest
    ): ResponseEntity<BookingResponse> {
        val booking = bookingService.cancelBooking(
            userId = principal.userId,
            bookingId = bookingId,
            reason = request.reason!!,
            version = request.version!!
        )
        return ResponseEntity.ok(BookingResponse.from(booking, razorpayGatewayClient.publicKeyId))
    }
}
