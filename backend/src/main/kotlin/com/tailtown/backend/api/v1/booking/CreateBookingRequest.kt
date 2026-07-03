package com.tailtown.backend.api.v1.booking

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateBookingRequest(

    @field:NotNull(message = "petId is required")
    val petId: UUID?,

    @field:NotNull(message = "vetId is required")
    val vetId: UUID?,

    @field:NotNull(message = "slotId is required")
    val slotId: UUID?,

    @field:NotBlank(message = "serviceType is required")
    val serviceType: String?,

    @field:NotBlank(message = "visitType is required")
    val visitType: String?,

    val addressId: UUID? = null,

    val notes: String? = null
)
