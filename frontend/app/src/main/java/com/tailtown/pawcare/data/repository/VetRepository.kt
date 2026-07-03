package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.vet.Vet

interface VetRepository {
    suspend fun getVets(): List<Vet>
    suspend fun getVet(id: String): Vet?
    suspend fun isSaved(vetId: String): Boolean
    suspend fun toggleSave(vetId: String): Boolean
}
