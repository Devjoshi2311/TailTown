package com.tailtown.backend.api.v1.booking

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CancelBookingRequest(

    @field:NotBlank(message = "reason is required")
    @field:Size(min = 3, max = 500, message = "reason must be between 3 and 500 characters")
    val reason: String?,

    @field:NotNull(message = "version is required")
    val version: Long?
)
