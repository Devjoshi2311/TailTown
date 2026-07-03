package com.tailtown.pawcare.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class RegisterRequestDto(val email: String, val password: String, val name: String)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int = 0,
    val refreshExpiresIn: Int = 0,
    val user: UserProfileDto,
)

@Serializable
data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val referralCode: String = "",
    val emailVerified: Boolean = false,
    val phoneVerified: Boolean = false,
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken") val refreshToken: String,
)

@Serializable
data class FirebaseAuthRequestDto(
    @SerialName("idToken") val idToken: String,
    @SerialName("displayName") val displayName: String? = null,
)

@Serializable
data class LogoutRequestDto(
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("allDevices") val allDevices: Boolean = false,
)
