package com.tailtown.backend.api.v1.shop

import com.tailtown.backend.infrastructure.persistence.shop.CategoryEntity
import com.tailtown.backend.infrastructure.persistence.shop.ProductEntity
import java.math.BigDecimal
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val parentId: UUID?,
    val name: String,
    val slug: String,
    val description: String?,
    val sortOrder: Int,
    val imageUrl: String?
) {
    companion object {
        fun from(entity: CategoryEntity) = CategoryResponse(
            id = entity.id,
            parentId = entity.parentId,
            name = entity.name,
            slug = entity.slug,
            description = entity.description,
            sortOrder = entity.sortOrder,
            imageUrl = entity.imageUrl
        )
    }
}

data class ProductResponse(
    val id: UUID,
    val categoryId: UUID,
    val sku: String,
    val name: String,
    val slug: String,
    val brand: String?,
    val subtitle: String?,
    val description: String?,
    val price: BigDecimal,
    val mrp: BigDecimal,
    val currency: String,
    val stockQty: Int,
    val isActive: Boolean,
    val isBestseller: Boolean,
    val rating: BigDecimal,
    val reviewCount: Int,
    val imageUrl: String?,
    val subscriptionEligible: Boolean
) {
    companion object {
        fun from(entity: ProductEntity) = ProductResponse(
            id = entity.id,
            categoryId = entity.categoryId,
            sku = entity.sku,
            name = entity.name,
            slug = entity.slug,
            brand = entity.brand,
            subtitle = entity.subtitle,
            description = entity.description,
            price = entity.price,
            mrp = entity.mrp,
            currency = entity.currency,
            stockQty = entity.stockQty,
            isActive = entity.isActive,
            isBestseller = entity.isBestseller,
            rating = entity.rating,
            reviewCount = entity.reviewCount,
            imageUrl = entity.imageUrl,
            subscriptionEligible = entity.subscriptionEligible
        )
    }
}
