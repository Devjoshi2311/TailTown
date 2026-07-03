package com.tailtown.backend.infrastructure.persistence.auth

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserEntity(

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "password_hash")
    var passwordHash: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "status", nullable = false)
    var status: String = "ACTIVE",

    @Column(name = "email_verified_at")
    var emailVerifiedAt: Instant? = null,

    @Column(name = "phone_verified_at")
    var phoneVerifiedAt: Instant? = null,

    @Column(name = "last_login_at")
    var lastLoginAt: Instant? = null,

    @Column(name = "marketing_consent", nullable = false)
    var marketingConsent: Boolean = false,

    @Column(name = "terms_version")
    var termsVersion: String? = null,

    @Column(name = "privacy_version")
    var privacyVersion: String? = null,

    @Column(name = "firebase_uid", unique = true)
    var firebaseUid: String? = null,

    @Column(name = "referral_code", nullable = false, unique = true)
    var referralCode: String

) : AuditableEntity() {

    @Transient
    var roles: List<String> = listOf("USER")

    val emailVerified: Boolean get() = emailVerifiedAt != null
    val phoneVerified: Boolean get() = phoneVerifiedAt != null
}
