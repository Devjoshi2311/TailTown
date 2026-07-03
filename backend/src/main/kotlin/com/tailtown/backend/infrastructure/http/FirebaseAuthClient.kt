package com.tailtown.backend.infrastructure.http

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tailtown.backend.platform.exception.AuthenticationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FirebaseAuthClient(private val firebaseApp: FirebaseApp?) {

    private val log = LoggerFactory.getLogger(FirebaseAuthClient::class.java)

    data class FirebaseUserInfo(
        val uid: String,
        val email: String?,
        val displayName: String?,
        val emailVerified: Boolean,
    )

    fun verifyIdToken(idToken: String): FirebaseUserInfo {
        if (firebaseApp == null) {
            log.warn("Firebase not configured — accepting idToken as UID (dev only, never do this in production)")
            return FirebaseUserInfo(
                uid = idToken.take(128),
                email = null,
                displayName = null,
                emailVerified = false,
            )
        }
        return try {
            val decoded = FirebaseAuth.getInstance(firebaseApp).verifyIdToken(idToken)
            FirebaseUserInfo(
                uid = decoded.uid,
                email = decoded.email,
                displayName = decoded.name,
                emailVerified = decoded.isEmailVerified,
            )
        } catch (ex: Exception) {
            throw AuthenticationException("Firebase token verification failed: ${ex.message}")
        }
    }
}
