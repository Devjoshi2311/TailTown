package com.tailtown.pawcare.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    data class FilterChip(val label: String, val categoryId: String?)

    data class CategoryUiState(
        val isLoading: Boolean = true,
        val filters: List<FilterChip> = listOf(FilterChip("All", null)),
        val selectedFilter: String = "All",
        val products: List<ShopProduct> = emptyList(),
        val categoryLabel: String = "",
    )

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private var parentCategoryId: String? = null

    fun init(slug: String) {
        if (parentCategoryId != null) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val allCategories = api.getCategories().data ?: emptyList()
                val parent = allCategories.firstOrNull { it.slug.equals(slug, ignoreCase = true) }
                parentCategoryId = parent?.id

                val children = allCategories.filter { it.parentId != null && it.parentId == parentCategoryId }
                val filters = buildList {
                    add(FilterChip("All", null))
                    children.forEach { add(FilterChip(it.name, it.id)) }
                }

                _uiState.value = _uiState.value.copy(
                    filters = filters,
                    categoryLabel = parent?.name ?: slug.replaceFirstChar { it.uppercaseChar() },
                )

                loadProducts(parentCategoryId)
            } catch (e: Exception) {
                android.util.Log.e("CategoryViewModel", "init: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun selectFilter(chip: FilterChip) {
        _uiState.value = _uiState.value.copy(selectedFilter = chip.label)
        viewModelScope.launch {
            loadProducts(chip.categoryId ?: parentCategoryId)
        }
    }

    private suspend fun loadProducts(categoryId: String?) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            val result = api.getProducts(categoryId = categoryId).data
                ?.map { it.toShopProduct() } ?: emptyList()
            _uiState.value = _uiState.value.copy(products = result, isLoading = false)
        } catch (e: Exception) {
            android.util.Log.e("CategoryViewModel", "loadProducts: ${e.message}")
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
