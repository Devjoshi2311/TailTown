package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.NotifPrefsRequestDto
import com.tailtown.pawcare.ui.account.NotificationPrefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteNotificationRepository @Inject constructor(private val api: ApiService) {

    suspend fun getPrefs(): NotificationPrefs {
        val dto = api.getNotifPrefs().data
            ?: return NotificationPrefs()
        return NotificationPrefs(
            appointments = dto.appointments,
            medications = dto.medications,
            orders = dto.orders,
            promotions = dto.promos,
        )
    }

    suspend fun updatePrefs(prefs: NotificationPrefs): NotificationPrefs {
        val dto = api.updateNotifPrefs(
            NotifPrefsRequestDto(
                appointments = prefs.appointments,
                medications = prefs.medications,
                orders = prefs.orders,
                promos = prefs.promotions,
            )
        ).data ?: return prefs
        return NotificationPrefs(
            appointments = dto.appointments,
            medications = dto.medications,
            orders = dto.orders,
            promotions = dto.promos,
        )
    }
}
