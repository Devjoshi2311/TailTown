package com.tailtown.backend.application.profile

import com.tailtown.backend.infrastructure.persistence.auth.UserEntity
import com.tailtown.backend.infrastructure.persistence.profile.AddressEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class UserProfileResponse(
    val id: UUID,
    val email: String,
    val phone: String?,
    val name: String,
    val avatarUrl: String?,
    val referralCode: String,
    val emailVerified: Boolean,
    val phoneVerified: Boolean,
    val version: Long
) {
    companion object {
        fun from(entity: UserEntity): UserProfileResponse = UserProfileResponse(
            id = entity.id,
            email = entity.email,
            phone = entity.phone,
            name = entity.name,
            avatarUrl = entity.avatarUrl,
            referralCode = entity.referralCode,
            emailVerified = entity.emailVerified,
            phoneVerified = entity.phoneVerified,
            version = entity.version
        )
    }
}

data class AddressResponse(
    val id: UUID,
    val label: String,
    val recipientName: String?,
    val phone: String?,
    val line1: String,
    val line2: String?,
    val landmark: String?,
    val city: String,
    val state: String,
    val pincode: String,
    val country: String,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val isDefault: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun from(entity: AddressEntity): AddressResponse = AddressResponse(
            id = entity.id,
            label = entity.label,
            recipientName = entity.recipientName,
            phone = entity.phone,
            line1 = entity.line1,
            line2 = entity.line2,
            landmark = entity.landmark,
            city = entity.city,
            state = entity.state,
            pincode = entity.pincode,
            country = entity.country,
            latitude = entity.latitude,
            longitude = entity.longitude,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            version = entity.version
        )
    }
}
