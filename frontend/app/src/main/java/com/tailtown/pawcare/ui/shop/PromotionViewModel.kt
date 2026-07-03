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

data class Promotion(
    val id: String,
    val badge: String,
    val title: String,
    val description: String,
    val ctaLabel: String,
    val endsAt: String,
)

@HiltViewModel
class PromotionViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _promotion = MutableStateFlow<Promotion?>(null)
    val promotion: StateFlow<Promotion?> = _promotion.asStateFlow()

    init { loadPromotions() }

    private fun loadPromotions() {
        viewModelScope.launch {
            runCatching { api.getPromotions() }
                .onSuccess { resp ->
                    val first = resp.data?.firstOrNull() ?: return@onSuccess
                    _promotion.value = Promotion(
                        id = first.id,
                        badge = first.badge,
                        title = first.title,
                        description = first.description,
                        ctaLabel = first.ctaLabel,
                        endsAt = first.endsAt,
                    )
                }
        }
    }
}
