package com.tailtown.backend.api.v1.notifications

import com.tailtown.backend.application.notifications.NotificationService
import com.tailtown.backend.infrastructure.persistence.notifications.NotificationEntity
import com.tailtown.backend.infrastructure.persistence.notifications.NotificationPreferencesEntity
import com.tailtown.backend.platform.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping
    fun getNotifications(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "false") unreadOnly: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<NotificationResponse> {
        val pageable = PageRequest.of(page, size)
        val result = notificationService.getNotifications(principal.userId, unreadOnly, pageable)
        return result.content.map { it.toResponse() }
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markRead(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @RequestBody(required = false) request: MarkReadRequest?
    ) {
        notificationService.markRead(principal.userId, id, request?.version)
    }

    @GetMapping("/preferences")
    fun getPreferences(
        @AuthenticationPrincipal principal: UserPrincipal
    ): NotificationPreferencesResponse {
        val prefs = notificationService.getPreferences(principal.userId)
        return prefs.toResponse()
    }

    @PutMapping("/preferences")
    fun updatePreferences(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdatePreferencesRequest
    ): NotificationPreferencesResponse {
        val prefs = notificationService.updatePreferences(
            userId = principal.userId,
            appointments = request.appointments,
            medications = request.medications,
            orders = request.orders,
            promos = request.promos,
            chat = request.chat,
            version = request.version!!
        )
        return prefs.toResponse()
    }

    @PostMapping("/push-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun registerPushToken(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: PushTokenRequest
    ) {
        notificationService.registerPushToken(
            userId = principal.userId,
            deviceId = request.deviceId!!,
            token = request.token!!,
            platform = request.platform
        )
    }

    private fun NotificationEntity.toResponse() = NotificationResponse(
        id = id,
        type = type,
        title = title,
        body = body,
        deepLink = deepLink,
        priority = priority,
        isRead = isRead,
        createdAt = createdAt,
        version = version
    )

    private fun NotificationPreferencesEntity.toResponse() = NotificationPreferencesResponse(
        appointments = appointments,
        medications = medications,
        orders = orders,
        promos = promos,
        chat = chat,
        version = version
    )
}
