package com.tailtown.pawcare.data.remote

import androidx.compose.ui.graphics.Color
import com.tailtown.pawcare.data.remote.dto.VetResponseDto
import com.tailtown.pawcare.data.repository.VetRepository
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.vet.Vet
import javax.inject.Inject
import javax.inject.Singleton

private val vetHeroTints = listOf(
    CoralSoft,
    Color(0xFFE8F4FF),
    Color(0xFFD6EFE8),
    Color(0xFFDDE5F4),
    Color(0xFFF5EACF),
)

private val savedVets = mutableSetOf<String>()

@Singleton
class RemoteVetRepository @Inject constructor(private val api: ApiService) : VetRepository {

    override suspend fun getVets(): List<Vet> =
        api.getVets().data?.mapIndexed { i, dto -> dto.toVet(i) } ?: emptyList()

    override suspend fun getVet(id: String): Vet? {
        val all = api.getVets().data ?: return null
        val idx = all.indexOfFirst { it.id == id }
        return if (idx >= 0) all[idx].toVet(idx) else null
    }

    override suspend fun isSaved(vetId: String) = vetId in savedVets

    override suspend fun toggleSave(vetId: String): Boolean =
        if (vetId in savedVets) { savedVets.remove(vetId); false }
        else { savedVets.add(vetId); true }
}

fun VetResponseDto.toVet(index: Int = 0): Vet {
    val seed = id.take(8)
    val galleryImages = buildList {
        avatarUrl?.let { add(it) }
        add("https://picsum.photos/seed/$seed-b/400/280")
        add("https://picsum.photos/seed/$seed-c/400/280")
        add("https://picsum.photos/seed/$seed-d/400/280")
        if (isEmpty()) add("https://picsum.photos/seed/$seed-a/400/280")
    }
    return Vet(
        id = id,
        name = displayName,
        specialty = specialty ?: "",
        location = city ?: clinicName ?: "",
        fullLocation = listOfNotNull(clinicName, city, state).joinToString(", "),
        rating = rating,
        reviewCount = reviewCount,
        yearsExperience = yearsExperience,
        languages = emptyList(),
        pricePerVisit = 600,
        homeVisitAvailable = homeVisitAvailable,
        isSuperhost = rating >= 4.8f,
        certifications = bio ?: "",
        heroTint = vetHeroTints[index % vetHeroTints.size],
        imageUrl = avatarUrl,
        images = galleryImages,
    )
}
