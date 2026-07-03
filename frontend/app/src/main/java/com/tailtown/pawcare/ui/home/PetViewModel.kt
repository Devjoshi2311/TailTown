package com.tailtown.pawcare.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.TokenStore
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.dto.CreatePetRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _petName   = MutableStateFlow("your pet")
    private val _petBreed  = MutableStateFlow("")
    private val _petGender = MutableStateFlow("")
    private val _petAge    = MutableStateFlow("")
    private val _petWeight = MutableStateFlow("")

    val petName:   StateFlow<String> = _petName.asStateFlow()
    val petBreed:  StateFlow<String> = _petBreed.asStateFlow()
    val petGender: StateFlow<String> = _petGender.asStateFlow()
    val petAge:    StateFlow<String> = _petAge.asStateFlow()
    val petWeight: StateFlow<String> = _petWeight.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStore.accessTokenFlow
                .filterNotNull()
                .distinctUntilChanged()
                .collect { loadPets() }
        }
    }

    fun createPet(name: String, breed: String, ageText: String, species: String, weightText: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val weightKg = weightText.trim().toFloatOrNull()
            val ageYears = ageText.trim().filter { it.isDigit() }.toIntOrNull()
            val dateOfBirth = ageYears?.let {
                java.time.LocalDate.now().minusYears(it.toLong()).toString()
            }
            runCatching {
                api.createPet(
                    CreatePetRequestDto(
                        name = name.trim(),
                        breed = breed.trim().ifBlank { null },
                        species = species,
                        weightKg = weightKg,
                        dateOfBirth = dateOfBirth,
                    )
                )
            }.onSuccess {
                Log.d("PetViewModel", "createPet success: ${it.data}")
                loadPets()
            }.onFailure { e ->
                Log.e("PetViewModel", "createPet failed: ${e::class.simpleName} — ${e.message}", e)
            }
            onDone()
        }
    }

    private fun loadPets() {
        viewModelScope.launch {
            runCatching { api.getPets() }
                .onSuccess { resp ->
                    Log.d("PetViewModel", "getPets success: data=${resp.data}")
                    val first = resp.data?.firstOrNull() ?: run {
                        Log.d("PetViewModel", "getPets: data is null or empty list")
                        return@onSuccess
                    }
                    Log.d("PetViewModel", "first pet: $first")
                    _petName.value   = first.name
                    _petBreed.value  = first.breed.orEmpty()
                    _petGender.value = first.gender?.lowercase()?.replaceFirstChar { it.uppercaseChar() }.orEmpty()
                    _petAge.value    = first.ageYears?.let { "$it yr" }.orEmpty()
                    _petWeight.value = first.weightKg?.let { "%.1fkg".format(it) }.orEmpty()
                }
                .onFailure { e ->
                    Log.e("PetViewModel", "getPets failed: ${e::class.simpleName} — ${e.message}", e)
                }
        }
    }
}
