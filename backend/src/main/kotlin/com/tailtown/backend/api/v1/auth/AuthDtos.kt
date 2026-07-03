package com.tailtown.backend.api.v1.auth

import com.tailtown.backend.infrastructure.persistence.auth.UserEntity
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

// ── Requests ────────────────────────────────────────────────────────────────

data class RegisterRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 8)
    val password: String,

    @field:NotBlank
    @field:Size(min = 2, max = 160)
    val name: String,

    val referralCode: String? = null
)

data class LoginRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String
)

data class RefreshTokenRequest(
    @field:NotBlank
    val refreshToken: String
)

data class FirebaseAuthRequest(
    @field:NotBlank
    val idToken: String,

    val displayName: String? = null
)

data class LogoutRequest(
    val refreshToken: String? = null,
    val allDevices: Boolean = false
)

// ── Responses ────────────────────────────────────────────────────────────────

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val refreshExpiresIn: Int,
    val user: UserProfileResponse
)

data class UserProfileResponse(
    val id: UUID,
    val email: String,
    val phone: String?,
    val name: String,
    val avatarUrl: String?,
    val referralCode: String,
    val emailVerified: Boolean,
    val phoneVerified: Boolean
)

// ── Mappers ──────────────────────────────────────────────────────────────────

fun UserEntity.toProfileResponse(): UserProfileResponse = UserProfileResponse(
    id = this.id,
    email = this.email,
    phone = this.phone,
    name = this.name,
    avatarUrl = this.avatarUrl,
    referralCode = this.referralCode,
    emailVerified = this.emailVerified,
    phoneVerified = this.phoneVerified
)
