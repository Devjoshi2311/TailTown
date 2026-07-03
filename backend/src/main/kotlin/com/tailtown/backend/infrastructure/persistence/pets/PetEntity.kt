package com.tailtown.backend.infrastructure.persistence.pets

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "pets")
@EntityListeners(AuditingEntityListener::class)
class PetEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "name", nullable = false, length = 120)
    var name: String,

    @Column(name = "species", nullable = false, length = 40)
    var species: String,

    @Column(name = "breed", length = 120)
    var breed: String? = null,

    @Column(name = "gender", length = 24)
    var gender: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = null,

    @Column(name = "weight_kg", precision = 6, scale = 2)
    var weightKg: BigDecimal? = null,

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    var avatarUrl: String? = null,

    @Column(name = "microchip_id", length = 80)
    var microchipId: String? = null,

    @Column(name = "neutered")
    var neutered: Boolean? = null,

    @Column(name = "allergies", columnDefinition = "TEXT")
    var allergies: String? = null,

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    var medicalNotes: String? = null

) : AuditableEntity()
