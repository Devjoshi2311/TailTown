package com.tailtown.backend.api.v1.shop

import com.tailtown.backend.application.shop.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val productService: ProductService
) {

    @GetMapping
    fun getCategories(
        @RequestParam(required = false) parentId: UUID?,
    ): ResponseEntity<List<CategoryResponse>> {
        val categories = productService.getCategories(parentId).map { CategoryResponse.from(it) }
        return ResponseEntity.ok(categories)
    }
}
