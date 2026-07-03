package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.TokenStore
import com.tailtown.pawcare.data.remote.dto.RefreshTokenRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val tokenRefreshApi: TokenRefreshApi,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStore.getAccessToken() }
        val request = chain.request().newBuilder()
            .apply { if (token != null) header("Authorization", "Bearer $token") }
            .build()

        val response = chain.proceed(request)

        // Don't attempt refresh on auth endpoints — prevents infinite loops
        val isAuthPath = chain.request().url.encodedPath.contains("/auth/")

        // Backend returns 403 (not 401) for expired JWT tokens. Only treat it as an
        // expired-token signal when we actually sent a token — genuine permission errors
        // (no token, wrong role) should pass through untouched.
        val isTokenExpiredError = (response.code == 401 || response.code == 403) && token != null
        if (!isTokenExpiredError || isAuthPath) return response

        val refreshToken = runBlocking { tokenStore.getRefreshToken() }
            ?: return response

        response.close()

        return try {
            val refreshResult = runBlocking {
                tokenRefreshApi.refresh(RefreshTokenRequestDto(refreshToken))
            }
            val newTokens = refreshResult.data
            if (newTokens != null) {
                runBlocking {
                    tokenStore.saveTokens(newTokens.accessToken, newTokens.refreshToken, newTokens.user.id)
                }
                val retryRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
                chain.proceed(retryRequest)
            } else {
                runBlocking { tokenStore.clearTokens() }
                chain.proceed(chain.request())
            }
        } catch (e: Exception) {
            runBlocking { tokenStore.clearTokens() }
            chain.proceed(chain.request())
        }
    }
}
