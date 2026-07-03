package com.tailtown.pawcare.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.TokenStore
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.RemoteAccountRepository
import com.tailtown.pawcare.data.remote.RemoteNotificationRepository
import com.tailtown.pawcare.data.remote.RemoteSubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepo: RemoteAccountRepository,
    private val subscriptionRepo: RemoteSubscriptionRepository,
    private val notifRepo: RemoteNotificationRepository,
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : ViewModel() {

    // ── Profile ───────────────────────────────────────────────────────────────
    private val _name  = MutableStateFlow("")
    private val _phone = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    val name:  StateFlow<String> = _name.asStateFlow()
    val phone: StateFlow<String> = _phone.asStateFlow()
    val email: StateFlow<String> = _email.asStateFlow()

    // ── Addresses ─────────────────────────────────────────────────────────────
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    // ── Payment methods ───────────────────────────────────────────────────────
    private val _paymentMethods = MutableStateFlow<List<SavedPaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<SavedPaymentMethod>> = _paymentMethods.asStateFlow()

    // ── Orders ────────────────────────────────────────────────────────────────
    private val _orders = MutableStateFlow<List<OrderSummary>>(emptyList())
    val orders: StateFlow<List<OrderSummary>> = _orders.asStateFlow()

    // ── Subscriptions ─────────────────────────────────────────────────────────
    private val _subscriptions = MutableStateFlow<List<SubscriptionItem>>(emptyList())
    val subscriptions: StateFlow<List<SubscriptionItem>> = _subscriptions.asStateFlow()

    // ── Notification prefs ────────────────────────────────────────────────────
    private val _notifPrefs = MutableStateFlow(NotificationPrefs())
    val notifPrefs: StateFlow<NotificationPrefs> = _notifPrefs.asStateFlow()

    // ── Referral ──────────────────────────────────────────────────────────────
    private val _referralInfo = MutableStateFlow<ReferralInfo?>(null)
    val referralInfo: StateFlow<ReferralInfo?> = _referralInfo.asStateFlow()

    // ── Pet count ─────────────────────────────────────────────────────────────
    private val _petCount = MutableStateFlow(0)
    val petCount: StateFlow<Int> = _petCount.asStateFlow()

    // ── Unread notification count ─────────────────────────────────────────────
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStore.accessTokenFlow
                .filterNotNull()
                .distinctUntilChanged()
                .collect { loadAll() }
        }
    }

    private fun loadAll() {
        loadProfile()
        loadAddresses()
        loadPaymentMethods()
        loadOrders()
        loadSubscriptions()
        loadNotifPrefs()
        loadReferral()
        loadUnreadCount()
        loadPetCount()
    }

    private fun loadProfile() = viewModelScope.launch {
        try {
            val (n, p, e) = accountRepo.getProfile()
            Log.d("AccountViewModel", "loadProfile: name=$n, phone=$p, email=$e")
            _name.value = n; _phone.value = p; _email.value = e
        } catch (e: Exception) {
            Log.e("AccountViewModel", "loadProfile failed: ${e::class.simpleName} — ${e.message}", e)
        }
    }

    private fun loadAddresses() = viewModelScope.launch {
        try { _addresses.value = accountRepo.getAddresses() } catch (_: Exception) {}
    }

    private fun loadPaymentMethods() = viewModelScope.launch {
        try { _paymentMethods.value = accountRepo.getPaymentMethods() } catch (_: Exception) {}
    }

    private fun loadOrders() = viewModelScope.launch {
        try { _orders.value = accountRepo.getOrders() } catch (_: Exception) {}
    }

    private fun loadSubscriptions() = viewModelScope.launch {
        try { _subscriptions.value = subscriptionRepo.getSubscriptions() } catch (_: Exception) {}
    }

    private fun loadNotifPrefs() = viewModelScope.launch {
        try { _notifPrefs.value = notifRepo.getPrefs() } catch (_: Exception) {}
    }

    private fun loadReferral() = viewModelScope.launch {
        try {
            val dto = api.getReferral().data ?: return@launch
            _referralInfo.value = ReferralInfo(
                code = dto.code,
                referrerReward = dto.referrerReward,
                refereeReward = dto.refereeReward,
                referralsMade = dto.referralsMade,
                rewardsEarned = dto.rewardsEarned,
            )
        } catch (_: Exception) {}
    }

    private fun loadPetCount() = viewModelScope.launch {
        try {
            val resp = api.getPets()
            Log.d("AccountViewModel", "loadPetCount: data=${resp.data}, size=${resp.data?.size}")
            _petCount.value = resp.data?.size ?: 0
        } catch (e: Exception) {
            Log.e("AccountViewModel", "loadPetCount failed: ${e::class.simpleName} — ${e.message}", e)
        }
    }

    fun reloadPetCount() = loadPetCount()

    private fun loadUnreadCount() = viewModelScope.launch {
        try {
            _unreadCount.value = api.getNotifications().data?.count { !it.isRead } ?: 0
        } catch (_: Exception) {}
    }

    fun updateProfile(name: String, phone: String, email: String) {
        viewModelScope.launch {
            try { accountRepo.updateProfile(name, phone) } catch (_: Exception) {}
            _name.value = name; _phone.value = phone; _email.value = email
        }
    }

    fun setDefaultAddress(id: String) {
        _addresses.update { list -> list.map { it.copy(isDefault = it.id == id) } }
    }

    fun deleteAddress(id: String) {
        _addresses.update { it.filter { a -> a.id != id } }
    }

    fun setDefaultPayment(id: String) {
        viewModelScope.launch {
            try { accountRepo.setDefaultPayment(id) } catch (_: Exception) {}
            _paymentMethods.update { list -> list.map { it.copy(isDefault = it.id == id) } }
        }
    }

    fun deletePayment(id: String) {
        viewModelScope.launch {
            try { accountRepo.deletePayment(id) } catch (_: Exception) {}
            _paymentMethods.update { it.filter { p -> p.id != id } }
        }
    }

    fun toggleSubscription(id: String) {
        viewModelScope.launch {
            try {
                val updated = subscriptionRepo.toggle(id)
                if (updated != null) {
                    _subscriptions.update { list -> list.map { if (it.id == id) updated else it } }
                }
            } catch (_: Exception) {
                _subscriptions.update { list -> list.map { if (it.id == id) it.copy(isActive = !it.isActive) else it } }
            }
        }
    }

    private fun updateNotifPrefs(prefs: NotificationPrefs) {
        viewModelScope.launch {
            try { _notifPrefs.value = notifRepo.updatePrefs(prefs) } catch (_: Exception) { _notifPrefs.value = prefs }
        }
    }

    fun setAppointmentNotif(on: Boolean) = updateNotifPrefs(_notifPrefs.value.copy(appointments = on))
    fun setMedicationNotif(on: Boolean)  = updateNotifPrefs(_notifPrefs.value.copy(medications = on))
    fun setOrderNotif(on: Boolean)       = updateNotifPrefs(_notifPrefs.value.copy(orders = on))
    fun setPromoNotif(on: Boolean)       = updateNotifPrefs(_notifPrefs.value.copy(promotions = on))
}
