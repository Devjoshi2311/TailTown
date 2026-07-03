package com.tailtown.pawcare.data.remote

import com.tailtown.pawcare.data.remote.dto.*
import com.tailtown.pawcare.ui.account.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAccountRepository @Inject constructor(private val api: ApiService) {

    suspend fun getProfile(): Triple<String, String, String> {
        val user = api.getMe().data ?: return Triple("", "", "")
        return Triple(user.name, user.phone.orEmpty(), user.email)
    }

    suspend fun updateProfile(name: String, phone: String) {
        api.updateMe(UpdateProfileRequestDto(name = name, phone = phone))
    }

    suspend fun getAddresses(): List<Address> =
        api.getAddresses().data?.map { it.toAddress() } ?: emptyList()

    suspend fun getPaymentMethods(): List<SavedPaymentMethod> =
        api.getPaymentMethods().data?.map { it.toPaymentMethod() } ?: emptyList()

    suspend fun setDefaultPayment(id: String) {
        api.setDefaultPayment(id)
    }

    suspend fun deletePayment(id: String) {
        api.deletePayment(id)
    }

    suspend fun getOrders(): List<OrderSummary> =
        api.getOrders().data?.map { it.toOrderSummary() } ?: emptyList()
}

private fun AddressResponseDto.toAddress() = Address(
    id = id,
    label = label,
    street = street,
    city = city,
    pincode = pincode,
    isDefault = isDefault,
)

private fun PaymentMethodResponseDto.toPaymentMethod() = SavedPaymentMethod(
    id = id,
    type = when (type.uppercase()) {
        "UPI" -> PaymentMethodType.UPI
        "CARD" -> PaymentMethodType.CARD
        else -> PaymentMethodType.NETBANKING
    },
    label = label,
    masked = masked,
    isDefault = isDefault,
)

private fun OrderResponseDto.toOrderSummary() = OrderSummary(
    id = id,
    dateLabel = placedAt?.take(10) ?: "",
    itemsLabel = items.joinToString(", ") { it.productName },
    total = grandTotal.toInt(),
    status = when (status.uppercase()) {
        "DELIVERED" -> OrderStatus.DELIVERED
        "IN_TRANSIT" -> OrderStatus.IN_TRANSIT
        "CANCELLED" -> OrderStatus.CANCELLED
        else -> OrderStatus.PROCESSING
    },
)
