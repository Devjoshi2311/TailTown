package com.tailtown.backend.common

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val size: Int,
    val totalPages: Int
) {
    companion object {
        fun <T> of(page: Page<T>): PageResponse<T> = PageResponse(
            items = page.content,
            total = page.totalElements,
            page = page.number,
            size = page.size,
            totalPages = page.totalPages
        )
    }
}
