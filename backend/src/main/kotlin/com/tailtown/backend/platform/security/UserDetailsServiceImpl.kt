package com.tailtown.backend.platform.security

import com.tailtown.backend.infrastructure.persistence.profile.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Spring Security calls this with the subject from the JWT, which is the userId as a UUID string.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val userId = try {
            UUID.fromString(username)
        } catch (ex: IllegalArgumentException) {
            throw UsernameNotFoundException("Invalid user identifier: $username")
        }
        return loadUserById(userId)
    }

    fun loadUserById(userId: UUID): UserPrincipal {
        val userEntity = userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw UsernameNotFoundException("User not found: $userId")

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return UserPrincipal(
            userId = userEntity.id,
            email = userEntity.email,
            roles = authorities
        )
    }
}
