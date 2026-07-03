package com.tailtown.backend.api.v1.auth

import com.tailtown.backend.application.auth.AuthService
import com.tailtown.backend.domain.auth.TokenPair
import com.tailtown.backend.infrastructure.persistence.auth.UserEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthResponse> {
        val (user, tokenPair) = authService.register(
            email = request.email,
            password = request.password,
            name = request.name,
            referralCode = request.referralCode
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(buildAuthResponse(user, tokenPair))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<AuthResponse> {
        val (user, tokenPair) = authService.login(
            email = request.email,
            password = request.password
        )
        return ResponseEntity.ok(buildAuthResponse(user, tokenPair))
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<AuthResponse> {
        val (user, tokenPair) = authService.refreshTokens(request.refreshToken)
        return ResponseEntity.ok(buildAuthResponse(user, tokenPair))
    }

    @PostMapping("/firebase")
    fun firebaseLogin(
        @Valid @RequestBody request: FirebaseAuthRequest
    ): ResponseEntity<AuthResponse> {
        val (user, tokenPair) = authService.loginWithFirebase(
            idToken = request.idToken,
            displayName = request.displayName
        )
        return ResponseEntity.ok(buildAuthResponse(user, tokenPair))
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody(required = false) request: LogoutRequest?
    ): ResponseEntity<Void> {
        val req = request ?: LogoutRequest()
        authService.logout(
            userId = principal.userId,
            refreshToken = req.refreshToken,
            allDevices = req.allDevices
        )
        return ResponseEntity.noContent().build()
    }

    private fun buildAuthResponse(user: UserEntity, tokenPair: TokenPair): AuthResponse =
        AuthResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            expiresIn = tokenPair.expiresIn,
            refreshExpiresIn = tokenPair.refreshExpiresIn,
            user = user.toProfileResponse()
        )
}
