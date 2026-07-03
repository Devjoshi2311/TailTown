package com.tailtown.backend.api.v1.profile

import com.tailtown.backend.application.profile.AddressRequest
import com.tailtown.backend.application.profile.AddressResponse
import com.tailtown.backend.application.profile.ProfileService
import com.tailtown.backend.application.profile.UpdateProfileRequest
import com.tailtown.backend.application.profile.UserProfileResponse
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
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping("/me")
    fun getUserProfile(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<UserProfileResponse> {
        val user = profileService.getProfile(principal.userId)
        return ResponseEntity.ok(UserProfileResponse.from(user))
    }

    @PatchMapping("/me")
    fun updateProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        val user = profileService.updateProfile(
            userId = principal.userId,
            name = request.name,
            phone = request.phone,
            avatarUrl = request.avatarUrl,
            version = request.version
        )
        return ResponseEntity.ok(UserProfileResponse.from(user))
    }

    @GetMapping("/addresses")
    fun getAddresses(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<List<AddressResponse>> {
        val addresses = profileService.getAddresses(principal.userId)
        return ResponseEntity.ok(addresses.map { AddressResponse.from(it) })
    }

    @PostMapping("/addresses")
    fun addAddress(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: AddressRequest
    ): ResponseEntity<AddressResponse> {
        val address = profileService.addAddress(principal.userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(AddressResponse.from(address))
    }

    @PatchMapping("/addresses/{addressId}")
    fun updateAddress(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable addressId: UUID,
        @Valid @RequestBody request: AddressRequest,
        @RequestHeader("X-Expected-Version") version: Long
    ): ResponseEntity<AddressResponse> {
        val address = profileService.updateAddress(
            userId = principal.userId,
            addressId = addressId,
            req = request,
            version = version
        )
        return ResponseEntity.ok(AddressResponse.from(address))
    }

    @DeleteMapping("/addresses/{addressId}")
    fun deleteAddress(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable addressId: UUID
    ): ResponseEntity<Void> {
        profileService.deleteAddress(principal.userId, addressId)
        return ResponseEntity.noContent().build()
    }
}
