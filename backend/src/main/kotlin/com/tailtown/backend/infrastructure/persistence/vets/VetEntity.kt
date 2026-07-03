package com.tailtown.backend.infrastructure.persistence.vets

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "vets")
class VetEntity(

    @Column(name = "display_name", nullable = false)
    var displayName: String,

    @Column(name = "specialty")
    var specialty: String? = null,

    @Column(name = "bio", columnDefinition = "TEXT")
    var bio: String? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "license_number")
    var licenseNumber: String? = null,

    @Column(name = "license_verified_at")
    var licenseVerifiedAt: Instant? = null,

    @Column(name = "status", nullable = false)
    var status: String = "ACTIVE",

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    var rating: BigDecimal = BigDecimal.ZERO,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,

    @Column(name = "years_experience", nullable = false)
    var yearsExperience: Int = 0,

    @Column(name = "home_visit_available", nullable = false)
    var homeVisitAvailable: Boolean = false,

    @Column(name = "clinic_name")
    var clinicName: String? = null,

    @Column(name = "address_line1")
    var addressLine1: String? = null,

    @Column(name = "city")
    var city: String? = null,

    @Column(name = "state")
    var state: String? = null,

    @Column(name = "pincode")
    var pincode: String? = null,

    @Column(name = "latitude", precision = 10, scale = 7)
    var latitude: BigDecimal? = null,

    @Column(name = "longitude", precision = 10, scale = 7)
    var longitude: BigDecimal? = null

) : AuditableEntity()
