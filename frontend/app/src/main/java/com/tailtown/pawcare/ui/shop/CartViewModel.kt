package com.tailtown.pawcare.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val shopRepository: ShopRepository) : ViewModel() {

    data class CartUiState(
        val items: List<CartItem> = emptyList(),
        val subtotal: Int = 0,
        val subscriptionSaving: Int = 0,
        val total: Int = 0,
        val itemCount: Int = 0,
    )

    private val _cartState = MutableStateFlow(CartUiState())
    val cartState: StateFlow<CartUiState> = _cartState.asStateFlow()

    // Items with an update in flight — a second tap on the same item while one is still
    // pending would race the first and lose to a 409 version conflict, silently dropping
    // the tap. Guarding here disables that button until the first request settles.
    private val _updatingIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingIds: StateFlow<Set<String>> = _updatingIds.asStateFlow()

    init { loadCart() }

    private fun loadCart() {
        viewModelScope.launch {
            val state = shopRepository.getCart()
            _cartState.value = state.toUiState()
        }
    }

    fun addItem(item: CartItem) {
        viewModelScope.launch {
            val state = shopRepository.addItem(item)
            _cartState.value = state.toUiState()
        }
    }

    fun increment(productId: String) = updateQty(productId, +1)

    fun decrement(productId: String) = updateQty(productId, -1)

    private fun updateQty(productId: String, delta: Int) {
        if (productId in _updatingIds.value) return
        _updatingIds.update { it + productId }
        viewModelScope.launch {
            try {
                val state = shopRepository.updateQty(productId, delta)
                _cartState.value = state.toUiState()
            } finally {
                _updatingIds.update { it - productId }
            }
        }
    }

    private fun com.tailtown.pawcare.data.repository.CartState.toUiState() = CartUiState(
        items = items,
        subtotal = subtotal,
        subscriptionSaving = subscriptionSaving,
        total = total,
        itemCount = items.sumOf { it.qty },
    )
}
