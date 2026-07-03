package com.tailtown.backend.application.profile

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(

    @field:Size(min = 1, max = 160, message = "Name must be between 1 and 160 characters")
    val name: String? = null,

    @field:Pattern(
        regexp = "^[+]?[0-9\\s\\-()]{7,20}$",
        message = "Phone number is not valid"
    )
    val phone: String? = null,

    val avatarUrl: String? = null,

    val version: Long? = null
)
