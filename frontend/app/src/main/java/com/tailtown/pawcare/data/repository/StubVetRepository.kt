package com.tailtown.pawcare.data.repository

import com.tailtown.pawcare.ui.vet.sampleVets

class StubVetRepository : VetRepository {
    private val saved = mutableSetOf<String>()
    override suspend fun getVets() = sampleVets
    override suspend fun getVet(id: String) = sampleVets.find { it.id == id }
    override suspend fun isSaved(vetId: String) = vetId in saved
    override suspend fun toggleSave(vetId: String): Boolean {
        return if (vetId in saved) { saved.remove(vetId); false }
        else { saved.add(vetId); true }
    }
}
