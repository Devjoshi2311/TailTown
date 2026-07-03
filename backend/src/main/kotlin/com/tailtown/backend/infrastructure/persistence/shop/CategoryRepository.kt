package com.tailtown.backend.infrastructure.persistence.shop

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<CategoryEntity, UUID> {

    fun findAllByIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc(): List<CategoryEntity>

    fun findAllByParentIdAndIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc(parentId: UUID): List<CategoryEntity>
}
