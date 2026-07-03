package com.tailtown.pawcare.common

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data
