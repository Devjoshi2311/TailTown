package com.tailtown.backend.application.booking

import com.tailtown.backend.application.payments.RazorpayGatewayClient
import com.tailtown.backend.infrastructure.persistence.booking.BookingEntity
import com.tailtown.backend.infrastructure.persistence.booking.BookingRepository
import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotRepository
import com.tailtown.backend.infrastructure.persistence.vets.VetRepository
import com.tailtown.backend.platform.exception.ForbiddenException
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.SlotUnavailableException
import com.tailtown.backend.platform.exception.ValidationException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class BookingService(
    private val bookingRepository: BookingRepository,
    private val bookingSlotRepository: BookingSlotRepository,
    private val vetRepository: VetRepository,
    private val razorpayGatewayClient: RazorpayGatewayClient
) {

    companion object {
        private val HOLD_DURATION: Duration = Duration.ofMinutes(15)
    }

    @Transactional(readOnly = true)
    fun listBookings(userId: UUID, pageable: Pageable): Page<BookingEntity> {
        return bookingRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)
    }

    @Transactional(readOnly = true)
    fun getBooking(userId: UUID, bookingId: UUID): BookingEntity {
        return bookingRepository.findByIdAndUserIdAndDeletedAtIsNull(bookingId, userId)
            ?: throw ResourceNotFoundException("Booking", bookingId)
    }

    fun createBooking(
        userId: UUID,
        petId: UUID,
        vetId: UUID,
        slotId: UUID,
        serviceType: String,
        visitType: String,
        addressId: UUID?,
        notes: String?
    ): BookingEntity {
        vetRepository.findByIdAndDeletedAtIsNull(vetId)
            ?: throw ResourceNotFoundException("Vet", vetId)

        val slot = bookingSlotRepository.findByIdForUpdate(slotId)
            ?: throw ResourceNotFoundException("BookingSlot", slotId)

        if (slot.status != "AVAILABLE") {
            throw SlotUnavailableException()
        }

        // Hold the slot for payment rather than booking it outright — released back to
        // AVAILABLE if payment fails, expires, or is never completed (see BookingPaymentService).
        slot.status = "HELD"
        slot.heldByUserId = userId
        slot.holdExpiresAt = Instant.now().plus(HOLD_DURATION)
        bookingSlotRepository.save(slot)

        var booking = bookingRepository.save(
            BookingEntity(
                userId = userId,
                petId = petId,
                vetId = vetId,
                slotId = slotId,
                serviceType = serviceType,
                visitType = visitType,
                scheduledStart = slot.startsAt,
                scheduledEnd = slot.endsAt,
                status = "PENDING_PAYMENT",
                amount = slot.price,
                currency = "INR",
                addressId = addressId,
                notes = notes
            )
        )

        // Create the matching Razorpay order using the server-computed slot price — never a
        // client-supplied amount. If this throws, the whole transaction rolls back (booking
        // row + slot hold), matching OrderService's equivalent flow.
        val amountPaise = slot.price.movePointRight(2).longValueExact()
        booking.razorpayOrderId = razorpayGatewayClient.createOrder(amountPaise, booking.currency, booking.id.toString())
        booking = bookingRepository.save(booking)

        return booking
    }

    fun cancelBooking(
        userId: UUID,
        bookingId: UUID,
        reason: String,
        version: Long
    ): BookingEntity {
        val booking = bookingRepository.findByIdAndUserIdAndDeletedAtIsNull(bookingId, userId)
            ?: throw ResourceNotFoundException("Booking", bookingId)

        if (booking.userId != userId) {
            throw ForbiddenException("You do not have permission to cancel this booking")
        }

        if (booking.version != version) {
            throw VersionConflictException()
        }

        val cancellableStatuses = setOf("CONFIRMED", "PENDING_PAYMENT")
        if (booking.status !in cancellableStatuses) {
            throw ValidationException("Booking with status '${booking.status}' cannot be cancelled")
        }

        booking.status = "CANCELLED"
        booking.cancelledAt = Instant.now()
        booking.cancelledBy = userId
        booking.cancellationReason = reason

        val slot = bookingSlotRepository.findByIdForUpdate(booking.slotId)
        if (slot != null && (slot.status == "BOOKED" || slot.status == "HELD")) {
            slot.status = "AVAILABLE"
            slot.heldByUserId = null
            slot.holdExpiresAt = null
            bookingSlotRepository.save(slot)
        }

        return bookingRepository.save(booking)
    }
}
