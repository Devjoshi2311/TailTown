package com.tailtown.backend.application.profile

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class AddressRequest(

    @field:NotBlank(message = "Label is required")
    @field:Size(max = 80, message = "Label must not exceed 80 characters")
    val label: String,

    @field:Size(max = 160, message = "Recipient name must not exceed 160 characters")
    val recipientName: String? = null,

    @field:Pattern(
        regexp = "^[+]?[0-9\\s\\-()]{7,20}$",
        message = "Phone number is not valid"
    )
    val phone: String? = null,

    @field:NotBlank(message = "Line 1 is required")
    val line1: String,

    val line2: String? = null,

    val landmark: String? = null,

    @field:NotBlank(message = "City is required")
    @field:Size(max = 100, message = "City must not exceed 100 characters")
    val city: String,

    @field:NotBlank(message = "State is required")
    @field:Size(max = 100, message = "State must not exceed 100 characters")
    val state: String,

    @field:NotBlank(message = "Pincode is required")
    @field:Size(max = 20, message = "Pincode must not exceed 20 characters")
    val pincode: String,

    @field:Size(min = 2, max = 2, message = "Country must be a 2-character ISO code")
    val country: String? = "IN",

    val latitude: BigDecimal? = null,

    val longitude: BigDecimal? = null,

    val isDefault: Boolean = false
)
