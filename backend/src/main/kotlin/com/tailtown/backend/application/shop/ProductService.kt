package com.tailtown.backend.application.shop

import com.tailtown.backend.infrastructure.persistence.shop.CategoryEntity
import com.tailtown.backend.infrastructure.persistence.shop.CategoryRepository
import com.tailtown.backend.infrastructure.persistence.shop.ProductEntity
import com.tailtown.backend.infrastructure.persistence.shop.ProductRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {

    fun getCategories(parentId: UUID? = null): List<CategoryEntity> =
        if (parentId != null)
            categoryRepository.findAllByParentIdAndIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc(parentId)
        else
            categoryRepository.findAllByIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc()

    fun getProducts(categoryId: UUID?, search: String?, page: Int, size: Int): Page<ProductEntity> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"))
        return when {
            categoryId != null && !search.isNullOrBlank() ->
                productRepository.findAllByCategoryIdAndSearchAndIsActiveTrueAndDeletedAtIsNull(
                    categoryId, search.trim(), pageable
                )
            categoryId != null ->
                productRepository.findAllByCategoryIdAndIsActiveTrueAndDeletedAtIsNull(categoryId, pageable)
            !search.isNullOrBlank() ->
                productRepository.findAllBySearchAndIsActiveTrueAndDeletedAtIsNull(search.trim(), pageable)
            else ->
                productRepository.findAllByIsActiveTrueAndDeletedAtIsNull(pageable)
        }
    }

    fun getProduct(productId: UUID): ProductEntity =
        productRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(productId)
            ?: throw ResourceNotFoundException("Product", productId)
}
