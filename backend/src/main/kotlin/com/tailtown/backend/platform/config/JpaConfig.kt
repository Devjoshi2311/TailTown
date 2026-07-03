package com.tailtown.backend.platform.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional
import java.util.UUID

@Configuration
@EnableJpaRepositories(basePackages = ["com.tailtown.backend.infrastructure.persistence"])
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaConfig {

    @Bean
    fun auditorProvider(): AuditorAware<UUID> {
        return AuditorAware {
            val authentication = SecurityContextHolder.getContext()?.authentication
            if (authentication == null || !authentication.isAuthenticated) {
                return@AuditorAware Optional.empty()
            }
            val principal = authentication.principal
            return@AuditorAware when (principal) {
                is String -> runCatching { Optional.of(UUID.fromString(principal)) }.getOrElse { Optional.empty() }
                is UUID -> Optional.of(principal)
                else -> {
                    runCatching {
                        val nameProperty = principal::class.java.getMethod("getName")
                        val name = nameProperty.invoke(principal) as? String
                        if (name != null) Optional.of(UUID.fromString(name)) else Optional.empty()
                    }.getOrElse { Optional.empty() }
                }
            }
        }
    }
}
