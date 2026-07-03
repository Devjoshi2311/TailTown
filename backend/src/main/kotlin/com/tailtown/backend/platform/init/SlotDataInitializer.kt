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

    @Transactional
    override fun run(args: ApplicationArguments) {
        val vets = vetRepository.findAllByStatusAndDeletedAtIsNull("ACTIVE", PageRequest.of(0, 100)).content
        if (vets.isEmpty()) return

        val today = LocalDate.now(ist)
        var created = 0

        for (vet in vets) {
            for (dayOffset in 1L..14L) {
                val date = today.plusDays(dayOffset)
                for (time in slotTimes) {
                    val startsAt = ZonedDateTime.of(date, time, ist).toInstant()
                    if (!slotRepository.existsByVetIdAndStartsAtAndDeletedAtIsNull(vet.id, startsAt)) {
                        slotRepository.save(
                            BookingSlotEntity(
                                vetId = vet.id,
                                serviceType = "CONSULTATION",
                                startsAt = startsAt,
                                endsAt = ZonedDateTime.of(date, time.plusMinutes(30), ist).toInstant(),
                                price = BigDecimal("600.00"),
                            )
                        )
                        created++
                    }
                }
            }
        }

        if (created > 0) log.info("SlotDataInitializer: created $created new slots for ${vets.size} vets")
    }
}
