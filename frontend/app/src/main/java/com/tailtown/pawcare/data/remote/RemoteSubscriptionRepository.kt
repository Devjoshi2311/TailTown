package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.PauseSubscriptionRequestDto
import com.tailtown.pawcare.data.remote.dto.ResumeSubscriptionRequestDto
import com.tailtown.pawcare.data.remote.dto.SubscriptionResponseDto
import com.tailtown.pawcare.ui.account.SubscriptionItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSubscriptionRepository @Inject constructor(private val api: ApiService) {

    suspend fun getSubscriptions(): List<SubscriptionItem> =
        api.getSubscriptions().data?.map { it.toItem() } ?: emptyList()

    suspend fun toggle(id: String): SubscriptionItem? {
        val current = api.getSubscriptions().data?.find { it.id == id } ?: return null
        return if (current.isActive) {
            api.pauseSubscription(id, PauseSubscriptionRequestDto()).data?.toItem()
        } else {
            api.resumeSubscription(id, ResumeSubscriptionRequestDto()).data?.toItem()
        }
    }
}

private fun SubscriptionResponseDto.toItem() = SubscriptionItem(
    id = id,
    productName = productName,
    variantLabel = variantLabel,
    nextDelivery = nextDelivery,
    pricePerCycle = pricePerCycle.toInt(),
    isActive = isActive,
)
