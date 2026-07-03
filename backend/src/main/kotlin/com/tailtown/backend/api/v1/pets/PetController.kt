package com.tailtown.backend.api.v1.pets

import com.tailtown.backend.application.pets.CreatePetRequest
import com.tailtown.backend.application.pets.PetResponse
import com.tailtown.backend.application.pets.PetService
import com.tailtown.backend.application.pets.UpdatePetRequest
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/pets")
class PetController(
    private val petService: PetService
) {

    @GetMapping
    fun listPets(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<List<PetResponse>> {
        val pets = petService.getPets(principal.userId)
        return ResponseEntity.ok(pets.map { PetResponse.from(it) })
    }

    @PostMapping
    fun createPet(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreatePetRequest
    ): ResponseEntity<PetResponse> {
        val pet = petService.createPet(principal.userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(PetResponse.from(pet))
    }

    @GetMapping("/{petId}")
    fun getPet(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID
    ): ResponseEntity<PetResponse> {
        val pet = petService.getPet(principal.userId, petId)
        return ResponseEntity.ok(PetResponse.from(pet))
    }

    @PatchMapping("/{petId}")
    fun updatePet(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID,
        @Valid @RequestBody request: UpdatePetRequest
    ): ResponseEntity<PetResponse> {
        val pet = petService.updatePet(
            userId = principal.userId,
            petId = petId,
            request = request,
            version = request.version
        )
        return ResponseEntity.ok(PetResponse.from(pet))
    }

    @DeleteMapping("/{petId}")
    fun deletePet(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable petId: UUID
    ): ResponseEntity<Void> {
        petService.deletePet(principal.userId, petId)
        return ResponseEntity.noContent().build()
    }
}
