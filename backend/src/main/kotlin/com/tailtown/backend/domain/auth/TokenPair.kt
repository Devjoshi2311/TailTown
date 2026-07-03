package com.tailtown.backend.domain.auth

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val refreshExpiresIn: Int
)
