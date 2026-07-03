package com.tailtown.pawcare.ui.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.common.UiState
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.repository.VetRepository
import com.tailtown.pawcare.ui.shop.ShopProduct
import com.tailtown.pawcare.ui.shop.toShopProduct
import com.tailtown.pawcare.ui.vet.Vet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vetRepository: VetRepository,
    private val api: ApiService,
) : ViewModel() {

    private val _vets = MutableStateFlow<UiState<List<Vet>>>(UiState.Loading)
    val vets: StateFlow<UiState<List<Vet>>> = _vets.asStateFlow()

    private val _savedIds = MutableStateFlow<Set<String>>(emptySet())
    val savedIds: StateFlow<Set<String>> = _savedIds.asStateFlow()

    private val _foodProducts = MutableStateFlow<List<ShopProduct>>(emptyList())
    val foodProducts: StateFlow<List<ShopProduct>> = _foodProducts.asStateFlow()

    private val _toyProducts = MutableStateFlow<List<ShopProduct>>(emptyList())
    val toyProducts: StateFlow<List<ShopProduct>> = _toyProducts.asStateFlow()

    private val _groomers = MutableStateFlow<List<Vet>>(emptyList())
    val groomers: StateFlow<List<Vet>> = _groomers.asStateFlow()

    private var foodLoaded = false
    private var toysLoaded = false
    private var groomLoaded = false

    init { loadVets() }

    fun loadVets() {
        viewModelScope.launch {
            _vets.value = UiState.Loading
            runCatching { vetRepository.getVets() }
                .onSuccess { _vets.value = UiState.Success(it) }
                .onFailure { _vets.value = UiState.Error(it.message ?: "Failed to load vets") }
        }
    }

    fun toggleSave(vetId: String) {
        viewModelScope.launch {
            val newState = vetRepository.toggleSave(vetId)
            _savedIds.update { ids -> if (newState) ids + vetId else ids - vetId }
        }
    }

    fun onTabSelected(tab: Int) {
        when (tab) {
            1 -> if (!foodLoaded) { foodLoaded = true; loadFood() }
            2 -> if (!toysLoaded) { toysLoaded = true; loadToys() }
            3 -> if (!groomLoaded) { groomLoaded = true; loadGroomers() }
        }
    }

    private fun loadFood() {
        viewModelScope.launch {
            try {
                val categoryId = findCategoryId("food")
                _foodProducts.value = api.getProducts(categoryId = categoryId).data
                    ?.map { it.toShopProduct() } ?: emptyList()
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "loadFood: ${e.message}")
            }
        }
    }

    private fun loadToys() {
        viewModelScope.launch {
            try {
                val categoryId = findCategoryId("toys")
                _toyProducts.value = api.getProducts(categoryId = categoryId).data
                    ?.map { it.toShopProduct() } ?: emptyList()
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "loadToys: ${e.message}")
            }
        }
    }

    private fun loadGroomers() {
        viewModelScope.launch {
            try {
                val dtos = api.getVets(specialty = "GROOMING").data ?: emptyList()
                _groomers.value = dtos.map { dto ->
                    Vet(
                        id = dto.id,
                        name = dto.displayName,
                        specialty = dto.specialty ?: "Grooming",
                        location = dto.city ?: "",
                        fullLocation = listOfNotNull(dto.city, dto.state).joinToString(", "),
                        rating = dto.rating,
                        reviewCount = dto.reviewCount,
                        yearsExperience = dto.yearsExperience,
                        languages = emptyList(),
                        pricePerVisit = 0,
                        homeVisitAvailable = dto.homeVisitAvailable,
                        isSuperhost = false,
                        certifications = "",
                        heroTint = Color(0xFFD6EFE8),
                        imageUrl = dto.avatarUrl,
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "loadGroomers: ${e.message}")
            }
        }
    }

    private suspend fun findCategoryId(slug: String): String? = try {
        api.getCategories().data
            ?.firstOrNull { it.slug.equals(slug, ignoreCase = true) || it.name.equals(slug, ignoreCase = true) }
            ?.id
    } catch (_: Exception) { null }
}
