package com.tailtown.backend.platform.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val userDetailsServiceImpl: UserDetailsServiceImpl
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/payments/webhook",
                        "/actuator/health",
                        "/actuator/info",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    // Admin-only internal endpoints
                    .requestMatchers("/api/internal/**").hasRole("ADMIN")
                    // Everything else requires authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    @Profile("dev", "local", "test")
    fun devCorsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        config.maxAge = 3600L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        // In non-dev profiles this will be overridden by a prod-specific bean.
        // Fallback: lock down to same-origin.
        val config = CorsConfiguration()
        config.allowedOriginPatterns = listOf(
            "https://*.tailtown.com",
            "https://tailtown.com"
        )
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Request-Id",
            "X-Idempotency-Key"
        )
        config.allowCredentials = true
        config.maxAge = 3600L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager
}
