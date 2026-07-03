package com.tailtown.backend.application.vets

import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotEntity
import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotRepository
import com.tailtown.backend.infrastructure.persistence.vets.VetEntity
import com.tailtown.backend.infrastructure.persistence.vets.VetRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional(readOnly = true)
class VetService(
    private val vetRepository: VetRepository,
    private val bookingSlotRepository: BookingSlotRepository
) {

    fun listVets(city: String?, specialty: String?, page: Int, size: Int): Page<VetEntity> {
        val pageable = PageRequest.of(page, size)
        return when {
            specialty != null -> vetRepository.findAllBySpecialtyIgnoreCaseAndStatusAndDeletedAtIsNull(specialty, "ACTIVE", pageable)
            city != null      -> vetRepository.findByCityIgnoreCaseAndStatusAndDeletedAtIsNull(city, "ACTIVE", pageable)
            else              -> vetRepository.findAllByStatusAndDeletedAtIsNull("ACTIVE", pageable)
        }
    }

    fun getVet(vetId: UUID): VetEntity {
        return vetRepository.findByIdAndDeletedAtIsNull(vetId)
            ?: throw ResourceNotFoundException("Vet", vetId)
    }

    fun getSlots(vetId: UUID, from: Instant, to: Instant): List<BookingSlotEntity> {
        vetRepository.findByIdAndDeletedAtIsNull(vetId)
            ?: throw ResourceNotFoundException("Vet", vetId)
        return bookingSlotRepository.findAllByVetIdAndStartsAtBetweenAndStatusAndDeletedAtIsNull(
            vetId = vetId,
            from = from,
            to = to,
            status = "AVAILABLE"
        )
    }
}
