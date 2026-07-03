package com.tailtown.pawcare.ui.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.common.UiState
import com.tailtown.pawcare.data.repository.VetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetDirectoryViewModel @Inject constructor(private val vetRepository: VetRepository) : ViewModel() {

    private val _vets = MutableStateFlow<UiState<List<Vet>>>(UiState.Loading)
    val vets: StateFlow<UiState<List<Vet>>> = _vets.asStateFlow()

    init { loadVets() }

    fun loadVets() {
        viewModelScope.launch {
            _vets.value = UiState.Loading
            runCatching { vetRepository.getVets() }
                .onSuccess { _vets.value = UiState.Success(it) }
                .onFailure { _vets.value = UiState.Error(it.message ?: "Failed to load vets") }
        }
    }

    fun getVetById(id: String): Vet? = (_vets.value as? UiState.Success)?.data?.find { it.id == id }
}
