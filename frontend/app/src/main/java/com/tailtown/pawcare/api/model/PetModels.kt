package com.tailtown.pawcare.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Shared enum ────────────────────────────────────────────────────────────

@Serializable
enum class PetSpecies {
    @SerialName("dog")   DOG,
    @SerialName("cat")   CAT,
    @SerialName("bird")  BIRD,
    @SerialName("other") OTHER,
}

// ── GET /users/me/pets  or  GET /users/me/pets/{petId} ────────────────────
// Also embedded in BookingDto.

@Serializable
data class PetDto(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val species: PetSpecies,
    val breed: String? = null,
    @SerialName("age_years") val ageYears: Float? = null,      // e.g. 3.2
    @SerialName("photo_url") val photoUrl: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String,           // ISO-8601
    @SerialName("updated_at") val updatedAt: String,
)

// ── POST /users/me/pets  (AddPetScreen "Add {name}" button) ───────────────

@Serializable
data class CreatePetRequest(
    val name: String,
    val species: PetSpecies,
    val breed: String? = null,
    @SerialName("age_years") val ageYears: Float? = null,
)

// ── PUT /users/me/pets/{petId} ─────────────────────────────────────────────

@Serializable
data class UpdatePetRequest(
    val name: String? = null,
    val breed: String? = null,
    @SerialName("age_years") val ageYears: Float? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    val notes: String? = null,
)

// ── Response wrappers ──────────────────────────────────────────────────────

/** ApiResponse<PetListData> */
@Serializable
data class PetListData(
    val pets: List<PetDto>,
)
