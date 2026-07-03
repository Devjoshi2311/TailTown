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
@Table(name = "prescriptions")
@EntityListeners(AuditingEntityListener::class)
class PrescriptionEntity(

    @Column(name = "pet_id", nullable = false, columnDefinition = "uuid")
    var petId: UUID,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "vet_id", columnDefinition = "uuid")
    var vetId: UUID? = null,

    @Column(name = "booking_id", columnDefinition = "uuid")
    var bookingId: UUID? = null,

    @Column(name = "medication_name", nullable = false)
    var medicationName: String,

    @Column(name = "dosage", nullable = false)
    var dosage: String,

    @Column(name = "frequency", nullable = false)
    var frequency: String,

    @Column(name = "instructions", columnDefinition = "TEXT")
    var instructions: String? = null,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Column(name = "status", nullable = false, length = 40)
    var status: String = "ACTIVE",

    @Column(name = "prescribed_by_name")
    var prescribedByName: String? = null,

    @Column(name = "document_url", columnDefinition = "TEXT")
    var documentUrl: String? = null,

) : AuditableEntity()
