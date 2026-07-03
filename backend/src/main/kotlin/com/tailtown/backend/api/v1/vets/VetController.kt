package com.tailtown.backend.api.v1.vets

import com.tailtown.backend.application.vets.VetService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/v1/vets")
class VetController(
    private val vetService: VetService
) {

    @GetMapping
    fun listVets(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) specialty: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<VetResponse>> {
        val result = vetService.listVets(city, specialty, page, size)
        return ResponseEntity.ok(result.content.map { VetResponse.from(it) })
    }

    @GetMapping("/{vetId}")
    fun getVet(@PathVariable vetId: UUID): ResponseEntity<VetResponse> {
        val vet = vetService.getVet(vetId)
        return ResponseEntity.ok(VetResponse.from(vet))
    }

    @GetMapping("/{vetId}/slots")
    fun getSlots(
        @PathVariable vetId: UUID,
        @RequestParam from: Instant,
        @RequestParam to: Instant,
        @RequestParam(required = false) serviceType: String?
    ): ResponseEntity<List<SlotResponse>> {
        val slots = vetService.getSlots(vetId, from, to)
        val filtered = if (serviceType != null) {
            slots.filter { it.serviceType == serviceType }
        } else {
            slots
        }
        return ResponseEntity.ok(filtered.map { SlotResponse.from(it) })
    }
}
