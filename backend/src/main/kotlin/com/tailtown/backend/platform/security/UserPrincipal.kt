package com.tailtown.backend.platform.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class UserPrincipal(
    val userId: UUID,
    val email: String,
    val roles: Collection<GrantedAuthority> = emptyList(),
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = roles

    override fun getPassword(): String? = null

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
