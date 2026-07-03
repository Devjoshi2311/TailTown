package com.tailtown.backend.infrastructure.persistence.shop

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<ProductEntity, UUID> {

    fun findAllByIsActiveTrueAndDeletedAtIsNull(pageable: Pageable): Page<ProductEntity>

    fun findByIdAndIsActiveTrueAndDeletedAtIsNull(id: UUID): ProductEntity?

    fun findAllByCategoryIdAndIsActiveTrueAndDeletedAtIsNull(
        categoryId: UUID,
        pageable: Pageable
    ): Page<ProductEntity>

    @Query(
        """
        SELECT p FROM ProductEntity p
        WHERE p.isActive = true
          AND p.deletedAt IS NULL
          AND p.categoryId = :categoryId
          AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        """
    )
    fun findAllByCategoryIdAndSearchAndIsActiveTrueAndDeletedAtIsNull(
        @Param("categoryId") categoryId: UUID,
        @Param("search") search: String,
        pageable: Pageable
    ): Page<ProductEntity>

    @Query(
        """
        SELECT p FROM ProductEntity p
        WHERE p.isActive = true
          AND p.deletedAt IS NULL
          AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        """
    )
    fun findAllBySearchAndIsActiveTrueAndDeletedAtIsNull(
        @Param("search") search: String,
        pageable: Pageable
    ): Page<ProductEntity>
}
