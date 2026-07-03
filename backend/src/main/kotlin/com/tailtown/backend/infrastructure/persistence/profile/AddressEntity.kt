package com.tailtown.backend.infrastructure.persistence.profile

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "user_addresses")
@EntityListeners(AuditingEntityListener::class)
class AddressEntity(

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "label", length = 80)
    var label: String,

    @Column(name = "recipient_name", length = 160)
    var recipientName: String? = null,

    @Column(name = "phone", length = 32)
    var phone: String? = null,

    @Column(name = "line1", nullable = false, columnDefinition = "TEXT")
    var line1: String,

    @Column(name = "line2", columnDefinition = "TEXT")
    var line2: String? = null,

    @Column(name = "landmark", columnDefinition = "TEXT")
    var landmark: String? = null,

    @Column(name = "city", nullable = false, length = 100)
    var city: String,

    @Column(name = "state", nullable = false, length = 100)
    var state: String,

    @Column(name = "pincode", nullable = false, length = 20)
    var pincode: String,

    @Column(name = "country", nullable = false, length = 2)
    var country: String = "IN",

    @Column(name = "latitude", precision = 9, scale = 6)
    var latitude: BigDecimal? = null,

    @Column(name = "longitude", precision = 9, scale = 6)
    var longitude: BigDecimal? = null,

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false

) : AuditableEntity()
