package com.tailtown.backend.api.v1.vets

import com.tailtown.backend.infrastructure.persistence.vets.VetEntity
import java.math.BigDecimal
import java.util.UUID

data class VetResponse(
    val id: UUID,
    val displayName: String,
    val specialty: String?,
    val bio: String?,
    val avatarUrl: String?,
    val rating: BigDecimal,
    val reviewCount: Int,
    val yearsExperience: Int,
    val homeVisitAvailable: Boolean,
    val clinicName: String?,
    val city: String?,
    val state: String?,
    val pincode: String?
) {
    companion object {
        fun from(entity: VetEntity): VetResponse = VetResponse(
            id = entity.id,
            displayName = entity.displayName,
            specialty = entity.specialty,
            bio = entity.bio,
            avatarUrl = entity.avatarUrl,
            rating = entity.rating,
            reviewCount = entity.reviewCount,
            yearsExperience = entity.yearsExperience,
            homeVisitAvailable = entity.homeVisitAvailable,
            clinicName = entity.clinicName,
            city = entity.city,
            state = entity.state,
            pincode = entity.pincode
        )
    }
}
