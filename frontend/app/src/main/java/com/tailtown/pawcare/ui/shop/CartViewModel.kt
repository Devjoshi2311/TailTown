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

    fun increment(productId: String) {
        viewModelScope.launch {
            val state = shopRepository.updateQty(productId, +1)
            _cartState.value = state.toUiState()
        }
    }

    fun decrement(productId: String) {
        viewModelScope.launch {
            val state = shopRepository.updateQty(productId, -1)
            _cartState.value = state.toUiState()
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
