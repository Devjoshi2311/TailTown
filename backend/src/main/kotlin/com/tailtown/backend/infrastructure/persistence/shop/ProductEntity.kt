package com.tailtown.backend.infrastructure.persistence.shop

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener::class)
class ProductEntity(

    @Column(name = "category_id", nullable = false, columnDefinition = "uuid")
    var categoryId: UUID,

    @Column(name = "sku", nullable = false, unique = true)
    var sku: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "slug", nullable = false, unique = true)
    var slug: String,

    @Column(name = "brand")
    var brand: String? = null,

    @Column(name = "subtitle")
    var subtitle: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,

    @Column(name = "mrp", nullable = false, precision = 19, scale = 2)
    var mrp: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "INR",

    @Column(name = "stock_qty", nullable = false)
    var stockQty: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "is_bestseller", nullable = false)
    var isBestseller: Boolean = false,

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    var rating: BigDecimal = BigDecimal.ZERO,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "subscription_eligible", nullable = false)
    var subscriptionEligible: Boolean = false

) : AuditableEntity()
