package com.tailtown.pawcare.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(private val healthRepository: HealthRepository) : ViewModel() {

    private val _prescription = MutableStateFlow(samplePrescription)
    val prescription: StateFlow<PrescriptionRecord> = _prescription.asStateFlow()

    private val _weightPoints = MutableStateFlow(sampleWeightPoints)
    val weightPoints: StateFlow<List<WeightPoint>> = _weightPoints.asStateFlow()

    fun markDose(doseTime: String) {
        viewModelScope.launch {
            val updated = healthRepository.markDose(_prescription.value.id, doseTime)
            _prescription.value = updated
        }
    }

    fun logWeight(value: Float) {
        viewModelScope.launch {
            val point = healthRepository.logWeight(value)
            _weightPoints.update { points ->
                points.toMutableList().also { it[it.lastIndex] = point }
            }
        }
    }
}
