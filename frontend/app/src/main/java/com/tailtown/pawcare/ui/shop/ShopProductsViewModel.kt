package com.tailtown.pawcare.ui.shop

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.dto.ProductResponseDto
import com.tailtown.pawcare.ui.theme.CoralSoft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val categoryTints = mapOf(
    "food"    to CoralSoft,
    "toys"    to Color(0xFFD6EFE8),
    "apparel" to Color(0xFFDDE5F4),
    "meds"    to Color(0xFFDDE5F4),
    "bath"    to CoralSoft,
    "bedding" to Color(0xFFF5EACF),
    "litter"  to CoralSoft,
)

fun ProductResponseDto.toShopProduct() = ShopProduct(
    id           = id,
    name         = name,
    subtitle     = subtitle.ifBlank { brand },
    rating       = rating,
    reviewCount  = reviewCount,
    price        = price.toInt(),
    originalPrice = if (mrp > price) mrp.toInt() else null,
    discountPct  = discountPct,
    heroTint     = Color(0xFFF5F2EC),
    isBestseller = isBestseller,
    description  = description,
    imageUrl     = imageUrl,
)

@HiltViewModel
class ShopProductsViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _products = MutableStateFlow<List<ShopProduct>>(emptyList())
    val products: StateFlow<List<ShopProduct>> = _products.asStateFlow()

    private var loaded = false

    // Was eagerly fetched at NavGraph root regardless of whether Shop was ever opened —
    // now triggered lazily from MallHomeScreen/CategoryScreen (see NavGraph.kt).
    fun ensureLoaded() {
        if (loaded) return
        loaded = true
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = api.getProducts().data?.map { it.toShopProduct() } ?: emptyList()
            } catch (_: Exception) {}
        }
    }

    fun getById(id: String): ShopProduct? = _products.value.find { it.id == id }

    fun getByCategory(categoryId: String): List<ShopProduct> =
        if (categoryId == "all") _products.value
        else _products.value.filter { it.name.lowercase().contains(categoryId.lowercase()) || categoryId == it.name }
}
