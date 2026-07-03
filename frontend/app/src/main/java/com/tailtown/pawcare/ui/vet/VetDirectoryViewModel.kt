package com.tailtown.pawcare.ui.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.repository.VetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetDirectoryViewModel @Inject constructor(private val vetRepository: VetRepository) : ViewModel() {

    private val _vets = MutableStateFlow<List<Vet>>(emptyList())
    val vets: StateFlow<List<Vet>> = _vets.asStateFlow()

    init { loadVets() }

    private fun loadVets() {
        viewModelScope.launch {
            try { _vets.value = vetRepository.getVets() } catch (_: Exception) {}
        }
    }

    fun getVetById(id: String): Vet? = _vets.value.find { it.id == id }
}
