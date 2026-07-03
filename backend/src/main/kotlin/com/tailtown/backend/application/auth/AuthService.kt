package com.tailtown.backend.application.auth

import com.tailtown.backend.domain.auth.TokenPair
import com.tailtown.backend.infrastructure.http.FirebaseAuthClient
import com.tailtown.backend.infrastructure.persistence.auth.RefreshTokenEntity
import com.tailtown.backend.infrastructure.persistence.auth.RefreshTokenRepository
import com.tailtown.backend.infrastructure.persistence.auth.UserEntity
import com.tailtown.backend.infrastructure.persistence.auth.UserRepository
import com.tailtown.backend.platform.exception.AuthenticationException
import com.tailtown.backend.platform.exception.EmailAlreadyRegisteredException
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val firebaseAuthClient: FirebaseAuthClient,
) {

    companion object {
        private const val ACCESS_TOKEN_EXPIRY_SECONDS = 900
        private const val REFRESH_TOKEN_EXPIRY_DAYS = 30L
        private val REFERRAL_CHARS = ('A'..'Z') + ('0'..'9')
    }

    fun register(
        email: String,
        password: String,
        name: String,
        referralCode: String?
    ): Pair<UserEntity, TokenPair> {
        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw EmailAlreadyRegisteredException()
        }

        val passwordHash = passwordEncoder.encode(password)
        val uniqueReferralCode = generateUniqueReferralCode()

        val user = UserEntity(
            email = email,
            name = name,
            passwordHash = passwordHash,
            referralCode = uniqueReferralCode
        )

        val savedUser = userRepository.save(user)
        return savedUser to issueTokenPair(savedUser)
    }

    fun login(email: String, password: String): Pair<UserEntity, TokenPair> {
        val user = userRepository.findByEmailAndDeletedAtIsNull(email)
            ?: throw AuthenticationException("Invalid email or password")

        val hash = user.passwordHash
            ?: throw AuthenticationException("Invalid email or password")

        if (!passwordEncoder.matches(password, hash)) {
            throw AuthenticationException("Invalid email or password")
        }

        user.lastLoginAt = Instant.now()
        val savedUser = userRepository.save(user)
        return savedUser to issueTokenPair(savedUser)
    }

    fun refreshTokens(refreshToken: String): Pair<UserEntity, TokenPair> {
        val tokenEntity = refreshTokenRepository.findByTokenAndRevokedAtIsNullAndExpiresAtAfter(
            refreshToken,
            Instant.now()
        ) ?: throw AuthenticationException("Refresh token is invalid or expired")

        tokenEntity.revokedAt = Instant.now()
        refreshTokenRepository.save(tokenEntity)

        val user = userRepository.findByIdAndDeletedAtIsNull(tokenEntity.userId)
            ?: throw ResourceNotFoundException("User", tokenEntity.userId)

        return user to issueTokenPair(user)
    }

    fun logout(userId: UUID, refreshToken: String?, allDevices: Boolean) {
        if (allDevices) {
            val tokens = refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId)
            val now = Instant.now()
            tokens.forEach { it.revokedAt = now }
            refreshTokenRepository.saveAll(tokens)
        } else if (refreshToken != null) {
            val tokenEntity = refreshTokenRepository.findByTokenAndRevokedAtIsNullAndExpiresAtAfter(
                refreshToken,
                Instant.now()
            )
            if (tokenEntity != null) {
                tokenEntity.revokedAt = Instant.now()
                refreshTokenRepository.save(tokenEntity)
            }
        }
    }

    fun loginWithFirebase(idToken: String, displayName: String?): Pair<UserEntity, TokenPair> {
        val firebaseUser = firebaseAuthClient.verifyIdToken(idToken)

        val existingUser = userRepository.findByFirebaseUidAndDeletedAtIsNull(firebaseUser.uid)
        if (existingUser != null) {
            if (firebaseUser.emailVerified && firebaseUser.email != null && existingUser.emailVerifiedAt == null) {
                existingUser.emailVerifiedAt = Instant.now()
            }
            existingUser.lastLoginAt = Instant.now()
            val saved = userRepository.save(existingUser)
            return saved to issueTokenPair(saved)
        }

        val name = displayName?.takeIf { it.isNotBlank() }
            ?: firebaseUser.displayName?.takeIf { it.isNotBlank() }
            ?: "TailTown User"
        val email = firebaseUser.email ?: "${firebaseUser.uid}@firebase.tailtown.internal"
        val referralCode = generateUniqueReferralCode()

        val newUser = UserEntity(
            email = email,
            name = name,
            firebaseUid = firebaseUser.uid,
            referralCode = referralCode
        )
        if (firebaseUser.emailVerified && firebaseUser.email != null) {
            newUser.emailVerifiedAt = Instant.now()
        }
        newUser.lastLoginAt = Instant.now()

        val saved = userRepository.save(newUser)
        return saved to issueTokenPair(saved)
    }

    private fun issueTokenPair(user: UserEntity): TokenPair {
        val accessToken = jwtTokenProvider.generateAccessToken(
            userId = user.id,
            email = user.email,
            roles = listOf("USER")
        )
        val refreshTokenValue = jwtTokenProvider.generateRefreshToken()
        val expiresAt = Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS)

        val refreshTokenEntity = RefreshTokenEntity(
            userId = user.id,
            token = refreshTokenValue,
            expiresAt = expiresAt
        )
        refreshTokenRepository.save(refreshTokenEntity)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshTokenValue,
            expiresIn = ACCESS_TOKEN_EXPIRY_SECONDS,
            refreshExpiresIn = (REFRESH_TOKEN_EXPIRY_DAYS * 24 * 60 * 60).toInt()
        )
    }

    private fun generateUniqueReferralCode(): String {
        var code: String
        do {
            val suffix = (1..4).map { REFERRAL_CHARS.random() }.joinToString("")
            code = "TAIL$suffix"
        } while (userRepository.findByReferralCodeAndDeletedAtIsNull(code) != null)
        return code
    }
}
