package com.tailtown.backend.infrastructure.persistence.cart

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "cart_items")
@EntityListeners(AuditingEntityListener::class)
class CartItemEntity(

    @Column(name = "cart_id", nullable = false, columnDefinition = "uuid")
    var cartId: UUID,

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    var productId: UUID,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "price_snapshot", precision = 19, scale = 2)
    var priceSnapshot: BigDecimal? = null,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR"

) : AuditableEntity()
