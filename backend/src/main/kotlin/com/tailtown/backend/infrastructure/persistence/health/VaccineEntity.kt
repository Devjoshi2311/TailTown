package com.tailtown.backend.infrastructure.persistence.health

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "vaccines")
@EntityListeners(AuditingEntityListener::class)
class VaccineEntity(

    @Column(name = "pet_id", nullable = false, columnDefinition = "uuid")
    var petId: UUID,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "vet_id", columnDefinition = "uuid")
    var vetId: UUID? = null,

    @Column(name = "booking_id", columnDefinition = "uuid")
    var bookingId: UUID? = null,

    @Column(name = "vaccine_name", nullable = false)
    var vaccineName: String,

    @Column(name = "dose_label", length = 80)
    var doseLabel: String? = null,

    @Column(name = "due_date")
    var dueDate: LocalDate? = null,

    @Column(name = "administered_date")
    var administeredDate: LocalDate? = null,

    @Column(name = "status", nullable = false, length = 40)
    var status: String = "DUE",

    @Column(name = "provider_name")
    var providerName: String? = null,

    @Column(name = "certificate_url", columnDefinition = "TEXT")
    var certificateUrl: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

) : AuditableEntity()
