package com.tailtown.backend.platform.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class RateLimitFilter(
    private val rateLimitService: RateLimitService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    companion object {
        private const val HEADER_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining"
        private const val HEADER_RATE_LIMIT_LIMIT = "X-RateLimit-Limit"

        // Window sizes in seconds
        private const val WINDOW_15_MIN = 15L * 60
        private const val WINDOW_1_MIN = 60L

        // Request limits
        private const val LIMIT_OTP_SEND = 5
        private const val LIMIT_OTP_VERIFY = 10
        private const val LIMIT_AUTHENTICATED_GLOBAL = 300
        private const val LIMIT_UNAUTHENTICATED_GLOBAL = 60
    }

    private data class RateLimitPolicy(val limit: Int, val windowSeconds: Long)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // Determine the rate-limit key: prefer user identity, fall back to IP
        val authHeader = request.getHeader("Authorization")
        val rateLimitKey = resolveKey(authHeader, request.remoteAddr)

        val policy = resolvePolicy(path, rateLimitKey)

        val allowed = rateLimitService.isAllowed(
            key = "${rateLimitKey}:${policy.windowSeconds}:${policy.limit}",
            limit = policy.limit,
            windowSeconds = policy.windowSeconds
        )

        val remaining = rateLimitService.getRemainingRequests(
            key = "${rateLimitKey}:${policy.windowSeconds}:${policy.limit}",
            limit = policy.limit,
            windowSeconds = policy.windowSeconds
        )

        response.setHeader(HEADER_RATE_LIMIT_LIMIT, policy.limit.toString())

        if (!allowed) {
            response.setHeader(HEADER_RATE_LIMIT_REMAINING, "0")
            writeRateLimitedResponse(response)
            return
        }

        response.setHeader(HEADER_RATE_LIMIT_REMAINING, remaining.toString())
        filterChain.doFilter(request, response)
    }

    /**
     * If an Authorization header is present and the JWT is valid, use "user:<userId>" as the key.
     * Otherwise fall back to "ip:<remoteAddr>".
     */
    private fun resolveKey(authHeader: String?, remoteAddr: String): String {
        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            if (token.isNotBlank() && jwtTokenProvider.validateToken(token)) {
                return try {
                    val userId = jwtTokenProvider.getUserIdFromToken(token)
                    "user:$userId"
                } catch (ex: Exception) {
                    "ip:$remoteAddr"
                }
            }
        }
        return "ip:$remoteAddr"
    }

    /**
     * Determine rate-limit policy based on the request path.
     *
     * Hierarchy (most-specific first):
     *  - POST /api/v1/auth/otp  → 5 requests / 15 min
     *  - POST /api/v1/auth/verify → 10 requests / 15 min
     *  - Authenticated user (key starts with "user:") → 300 requests / 1 min
     *  - Unauthenticated  → 60 requests / 1 min
     */
    private fun resolvePolicy(path: String, key: String): RateLimitPolicy {
        return when {
            path.contains("/api/v1/auth/otp", ignoreCase = true) ->
                RateLimitPolicy(LIMIT_OTP_SEND, WINDOW_15_MIN)

            path.contains("/api/v1/auth/verify", ignoreCase = true) ->
                RateLimitPolicy(LIMIT_OTP_VERIFY, WINDOW_15_MIN)

            key.startsWith("user:") ->
                RateLimitPolicy(LIMIT_AUTHENTICATED_GLOBAL, WINDOW_1_MIN)

            else ->
                RateLimitPolicy(LIMIT_UNAUTHENTICATED_GLOBAL, WINDOW_1_MIN)
        }
    }

    private fun writeRateLimitedResponse(response: HttpServletResponse) {
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val body = mapOf(
            "code" to "RATE_LIMITED",
            "message" to "Too many requests"
        )
        response.writer.write(objectMapper.writeValueAsString(body))
        response.writer.flush()
    }
}
