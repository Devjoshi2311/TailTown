package com.tailtown.backend.infrastructure.persistence.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun findByEmailAndDeletedAtIsNull(email: String): UserEntity?

    fun findByFirebaseUidAndDeletedAtIsNull(firebaseUid: String): UserEntity?

    fun findByReferralCodeAndDeletedAtIsNull(referralCode: String): UserEntity?

    fun findByIdAndDeletedAtIsNull(id: UUID): UserEntity?

    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean

    fun existsByPhoneAndDeletedAtIsNull(phone: String): Boolean

    fun findByPhoneAndDeletedAtIsNull(phone: String): UserEntity?
}
