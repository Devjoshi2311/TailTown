package com.tailtown.backend.application.profile

import com.tailtown.backend.infrastructure.persistence.auth.UserEntity
import com.tailtown.backend.infrastructure.persistence.auth.UserRepository
import com.tailtown.backend.infrastructure.persistence.profile.AddressEntity
import com.tailtown.backend.infrastructure.persistence.profile.AddressRepository
import com.tailtown.backend.platform.exception.ConflictException
import com.tailtown.backend.platform.exception.ErrorCode
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.ValidationException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ProfileService(
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository
) {

    @Transactional(readOnly = true)
    fun getProfile(userId: UUID): UserEntity =
        userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw ResourceNotFoundException("User", userId)

    fun updateProfile(
        userId: UUID,
        name: String?,
        phone: String?,
        avatarUrl: String?,
        version: Long?
    ): UserEntity {
        val user = userRepository.findByIdAndDeletedAtIsNull(userId)
            ?: throw ResourceNotFoundException("User", userId)

        if (version != null && user.version != version) {
            throw VersionConflictException()
        }

        if (phone != null && phone != user.phone) {
            if (userRepository.existsByPhoneAndDeletedAtIsNull(phone)) {
                throw ConflictException(ErrorCode.PHONE_ALREADY_USED, "Phone number is already in use")
            }
            user.phone = phone
        }

        if (name != null) user.name = name
        if (avatarUrl != null) user.avatarUrl = avatarUrl

        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getAddresses(userId: UUID): List<AddressEntity> =
        addressRepository.findAllByUserIdAndDeletedAtIsNull(userId)

    fun addAddress(userId: UUID, req: AddressRequest): AddressEntity {
        if (addressRepository.existsByUserIdAndLabelAndDeletedAtIsNull(userId, req.label)) {
            throw ValidationException("Address label '${req.label}' already exists for this user")
        }

        if (req.isDefault) {
            clearExistingDefault(userId)
        }

        val entity = AddressEntity(
            userId = userId,
            label = req.label,
            recipientName = req.recipientName,
            phone = req.phone,
            line1 = req.line1,
            line2 = req.line2,
            landmark = req.landmark,
            city = req.city,
            state = req.state,
            pincode = req.pincode,
            country = req.country ?: "IN",
            latitude = req.latitude,
            longitude = req.longitude,
            isDefault = req.isDefault
        )
        return addressRepository.save(entity)
    }

    fun updateAddress(userId: UUID, addressId: UUID, req: AddressRequest, version: Long): AddressEntity {
        val address = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
            ?: throw ResourceNotFoundException("Address", addressId)

        if (address.version != version) {
            throw VersionConflictException()
        }

        if (req.label != address.label &&
            addressRepository.existsByUserIdAndLabelAndDeletedAtIsNull(userId, req.label)
        ) {
            throw ValidationException("Address label '${req.label}' already exists for this user")
        }

        if (req.isDefault && !address.isDefault) {
            clearExistingDefault(userId)
        }

        address.label = req.label
        address.recipientName = req.recipientName
        address.phone = req.phone
        address.line1 = req.line1
        address.line2 = req.line2
        address.landmark = req.landmark
        address.city = req.city
        address.state = req.state
        address.pincode = req.pincode
        address.country = req.country ?: "IN"
        address.latitude = req.latitude
        address.longitude = req.longitude
        address.isDefault = req.isDefault

        return addressRepository.save(address)
    }

    fun deleteAddress(userId: UUID, addressId: UUID) {
        val address = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
            ?: throw ResourceNotFoundException("Address", addressId)
        address.softDelete()
        addressRepository.save(address)
    }

    private fun clearExistingDefault(userId: UUID) {
        val existing = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userId)
        if (existing != null) {
            existing.isDefault = false
            addressRepository.save(existing)
        }
    }
}
