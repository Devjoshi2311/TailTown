package com.tailtown.backend.infrastructure.persistence.orders

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "order_items")
@EntityListeners(AuditingEntityListener::class)
class OrderItemEntity(

    @Column(name = "order_id", nullable = false, columnDefinition = "uuid")
    var orderId: UUID,

    @Column(name = "product_id", columnDefinition = "uuid")
    var productId: UUID? = null,

    @Column(name = "sku", nullable = false)
    var sku: String,

    @Column(name = "product_name", nullable = false)
    var productName: String,

    @Column(name = "product_image_url")
    var productImageUrl: String? = null,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    var unitPrice: BigDecimal,

    @Column(name = "line_discount", nullable = false, precision = 19, scale = 2)
    var lineDiscount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "line_tax", nullable = false, precision = 19, scale = 2)
    var lineTax: BigDecimal = BigDecimal.ZERO,

    @Column(name = "line_total", nullable = false, precision = 19, scale = 2)
    var lineTotal: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR"

) : AuditableEntity()
