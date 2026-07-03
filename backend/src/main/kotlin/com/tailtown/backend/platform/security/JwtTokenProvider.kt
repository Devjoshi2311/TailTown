package com.tailtown.backend.platform.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    @Value("\${jwt.private-key:}") private val privateKeyBase64: String,
    @Value("\${jwt.public-key:}") private val publicKeyBase64: String
) {

    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private lateinit var privateKey: RSAPrivateKey
    private lateinit var publicKey: RSAPublicKey

    companion object {
        private const val ACCESS_TOKEN_VALIDITY_MS = 15L * 60 * 1000 // 15 minutes
    }

    @PostConstruct
    fun init() {
        if (privateKeyBase64.isBlank() || publicKeyBase64.isBlank()) {
            generateInMemoryKeys()
            return
        }

        try {
            val keyFactory = KeyFactory.getInstance("RSA")

            val cleanedPrivate = privateKeyBase64
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("\\s".toRegex(), "")
            val privateKeyBytes = Base64.getDecoder().decode(cleanedPrivate)
            privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes)) as RSAPrivateKey

            val cleanedPublic = publicKeyBase64
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\\s".toRegex(), "")
            val publicKeyBytes = Base64.getDecoder().decode(cleanedPublic)
            publicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicKeyBytes)) as RSAPublicKey

            log.info("JWT RSA key pair loaded successfully.")
        } catch (ex: Exception) {
            log.error("Failed to load JWT RSA keys from config: {}. Falling back to in-memory keys — DO NOT use in production.", ex.message)
            generateInMemoryKeys()
        }
    }

    private fun generateInMemoryKeys() {
        log.warn("Generating in-memory RSA key pair for development. DO NOT use in production.")
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(2048)
        val kp = gen.generateKeyPair()
        privateKey = kp.private as RSAPrivateKey
        publicKey = kp.public as RSAPublicKey
    }

    fun generateAccessToken(userId: UUID, email: String, roles: List<String>): String {
        val now = Date()
        val expiry = Date(now.time + ACCESS_TOKEN_VALIDITY_MS)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(privateKey)
            .compact()
    }

    fun generateRefreshToken(): String = UUID.randomUUID().toString()

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (ex: SignatureException) {
            log.debug("Invalid JWT signature: {}", ex.message)
            false
        } catch (ex: io.jsonwebtoken.ExpiredJwtException) {
            log.debug("Expired JWT token: {}", ex.message)
            false
        } catch (ex: io.jsonwebtoken.MalformedJwtException) {
            log.debug("Malformed JWT token: {}", ex.message)
            false
        } catch (ex: io.jsonwebtoken.UnsupportedJwtException) {
            log.debug("Unsupported JWT token: {}", ex.message)
            false
        } catch (ex: IllegalArgumentException) {
            log.debug("JWT claims string is empty: {}", ex.message)
            false
        } catch (ex: Exception) {
            log.debug("JWT validation error: {}", ex.message)
            false
        }
    }

    fun getClaimsFromToken(token: String): Claims =
        Jwts.parser()
            .verifyWith(publicKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun getUserIdFromToken(token: String): UUID =
        UUID.fromString(getClaimsFromToken(token).subject)
}
