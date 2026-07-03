package com.tailtown.backend.platform.init

import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotEntity
import com.tailtown.backend.infrastructure.persistence.vets.BookingSlotRepository
import com.tailtown.backend.infrastructure.persistence.vets.VetRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class SlotDataInitializer(
    private val vetRepository: VetRepository,
    private val slotRepository: BookingSlotRepository,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)
    private val ist = ZoneId.of("Asia/Kolkata")

    private val slotTimes = listOf(
        LocalTime.of(10, 0),
        LocalTime.of(11, 30),
        LocalTime.of(14, 0),
    )

    // Runs on every boot, blocking readiness — was doing one exists-check + one insert per
    // slot (up to 2 * vets * 14 days * 3 times round trips, sequentially). Replaced with a
    // single bulk read of what already exists in the window, then one batched insert for the
    // gaps, so a cold Render start isn't held up by hundreds of sequential DB round trips.
    @Transactional
    override fun run(args: ApplicationArguments) {
        val vets = vetRepository.findAllByStatusAndDeletedAtIsNull("ACTIVE", PageRequest.of(0, 100)).content
        if (vets.isEmpty()) return

        val today = LocalDate.now(ist)
        val rangeStart = ZonedDateTime.of(today.plusDays(1), LocalTime.MIN, ist).toInstant()
        val rangeEnd = ZonedDateTime.of(today.plusDays(14), LocalTime.MAX, ist).toInstant()

        val existing = slotRepository
            .findAllByVetIdInAndStartsAtBetweenAndDeletedAtIsNull(vets.map { it.id }, rangeStart, rangeEnd)
            .mapTo(HashSet()) { it.vetId to it.startsAt }

        val toCreate = mutableListOf<BookingSlotEntity>()
        for (vet in vets) {
            for (dayOffset in 1L..14L) {
                val date = today.plusDays(dayOffset)
                for (time in slotTimes) {
                    val startsAt = ZonedDateTime.of(date, time, ist).toInstant()
                    if (vet.id to startsAt !in existing) {
                        toCreate += BookingSlotEntity(
                            vetId = vet.id,
                            serviceType = "CONSULTATION",
                            startsAt = startsAt,
                            endsAt = ZonedDateTime.of(date, time.plusMinutes(30), ist).toInstant(),
                            price = BigDecimal("600.00"),
                        )
                    }
                }
            }
        }

        if (toCreate.isNotEmpty()) {
            slotRepository.saveAll(toCreate)
            log.info("SlotDataInitializer: created ${toCreate.size} new slots for ${vets.size} vets")
        }
    }
}
