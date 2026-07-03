package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.ApiResponseDto
import com.tailtown.pawcare.data.remote.dto.AuthResponseDto
import com.tailtown.pawcare.data.remote.dto.RefreshTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshApi {
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequestDto): ApiResponseDto<AuthResponseDto>
}
