package com.tailtown.backend.infrastructure.persistence.health

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
@Table(name = "weight_records")
@EntityListeners(AuditingEntityListener::class)
class WeightRecordEntity(

    @Column(name = "pet_id", nullable = false, columnDefinition = "uuid")
    var petId: UUID,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "weight_kg", nullable = false, precision = 6, scale = 2)
    var weightKg: BigDecimal,

    @Column(name = "recorded_on", nullable = false)
    var recordedOn: LocalDate,

    @Column(name = "source", nullable = false, length = 40)
    var source: String = "USER",

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

) : AuditableEntity()
