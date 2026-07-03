package com.tailtown.backend.application.notifications

import com.tailtown.backend.infrastructure.persistence.notifications.NotificationEntity
import com.tailtown.backend.infrastructure.persistence.notifications.NotificationPreferencesEntity
import com.tailtown.backend.infrastructure.persistence.notifications.NotificationPreferencesRepository
import com.tailtown.backend.infrastructure.persistence.notifications.NotificationRepository
import com.tailtown.backend.infrastructure.persistence.notifications.PushTokenEntity
import com.tailtown.backend.infrastructure.persistence.notifications.PushTokenRepository
import com.tailtown.backend.platform.exception.ResourceNotFoundException
import com.tailtown.backend.platform.exception.VersionConflictException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationPreferencesRepository: NotificationPreferencesRepository,
    private val pushTokenRepository: PushTokenRepository
) {

    @Transactional(readOnly = true)
    fun getNotifications(userId: UUID, unreadOnly: Boolean, pageable: Pageable): Page<NotificationEntity> {
        return if (unreadOnly) {
            notificationRepository.findAllByUserIdAndIsReadFalseAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable)
        } else {
            notificationRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable)
        }
    }

    fun markRead(userId: UUID, notificationId: UUID, version: Long?) {
        val entity = notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
            .orElseThrow { ResourceNotFoundException("Notification", notificationId) }

        if (version != null && entity.version != version) throw VersionConflictException()

        if (!entity.isRead) {
            entity.isRead = true
            entity.readAt = Instant.now()
            notificationRepository.save(entity)
        }
    }

    @Transactional(readOnly = true)
    fun getPreferences(userId: UUID): NotificationPreferencesEntity {
        return notificationPreferencesRepository.findByUserId(userId)
            .orElseGet {
                notificationPreferencesRepository.save(
                    NotificationPreferencesEntity(userId = userId)
                )
            }
    }

    fun updatePreferences(
        userId: UUID,
        appointments: Boolean,
        medications: Boolean,
        orders: Boolean,
        promos: Boolean,
        chat: Boolean,
        version: Long
    ): NotificationPreferencesEntity {
        val entity = notificationPreferencesRepository.findByUserId(userId)
            .orElseGet { NotificationPreferencesEntity(userId = userId) }

        if (entity.version != version) throw VersionConflictException()

        entity.appointments = appointments
        entity.medications = medications
        entity.orders = orders
        entity.promos = promos
        entity.chat = chat
        return notificationPreferencesRepository.save(entity)
    }

    fun registerPushToken(userId: UUID, deviceId: String, token: String, platform: String) {
        val existing = pushTokenRepository.findByUserIdAndDeviceId(userId, deviceId)
        if (existing.isPresent) {
            val pushToken = existing.get()
            pushToken.token = token
            pushToken.platform = platform
            pushToken.isActive = true
            pushTokenRepository.save(pushToken)
        } else {
            pushTokenRepository.save(
                PushTokenEntity(
                    userId = userId,
                    deviceId = deviceId,
                    token = token,
                    platform = platform,
                    isActive = true
                )
            )
        }
    }
}
