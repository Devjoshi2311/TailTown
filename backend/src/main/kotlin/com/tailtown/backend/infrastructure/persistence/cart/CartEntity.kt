package com.tailtown.backend.infrastructure.persistence.cart

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "cart")
@EntityListeners(AuditingEntityListener::class)
class CartEntity(

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "status", nullable = false)
    var status: String = "ACTIVE",

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "expires_at")
    var expiresAt: Instant? = null

) : AuditableEntity()
