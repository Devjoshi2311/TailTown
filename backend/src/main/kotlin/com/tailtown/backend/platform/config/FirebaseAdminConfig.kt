package com.tailtown.backend.platform.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class FirebaseAdminConfig {

    private val log = LoggerFactory.getLogger(FirebaseAdminConfig::class.java)

    @Value("\${firebase.credentials-path:}")
    private val credentialsPath: String = ""

    @Value("\${firebase.credentials-json:}")
    private val credentialsJson: String = ""

    @Bean
    fun firebaseApp(): FirebaseApp? {
        val stream = when {
            credentialsPath.isNotBlank() -> {
                val file = File(credentialsPath)
                if (!file.exists()) {
                    log.error("Firebase credentials file not found: $credentialsPath")
                    return null
                }
                file.inputStream()
            }
            credentialsJson.isNotBlank() -> credentialsJson.toByteArray().inputStream()
            else -> {
                log.warn(
                    "Neither firebase.credentials-path nor firebase.credentials-json is set — " +
                    "Firebase token verification disabled. Set FIREBASE_CREDENTIALS_JSON in production."
                )
                return null
            }
        }

        if (FirebaseApp.getApps().isNotEmpty()) return FirebaseApp.getInstance()
        val credentials = GoogleCredentials.fromStream(stream)
        return FirebaseApp.initializeApp(
            FirebaseOptions.builder().setCredentials(credentials).build()
        )
    }
}
