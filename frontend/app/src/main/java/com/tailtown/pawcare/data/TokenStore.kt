package com.tailtown.pawcare.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "tailtown_secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _accessTokenFlow = MutableStateFlow(prefs.getString(KEY_ACCESS, null))

    val accessTokenFlow: Flow<String?> get() = _accessTokenFlow

    suspend fun saveTokens(access: String, refresh: String, userId: String = "") =
        withContext(Dispatchers.IO) {
            prefs.edit {
                putString(KEY_ACCESS, access)
                putString(KEY_REFRESH, refresh)
                if (userId.isNotEmpty()) putString(KEY_USER_ID, userId)
            }
            _accessTokenFlow.value = access
        }

    suspend fun clearTokens() = withContext(Dispatchers.IO) {
        prefs.edit {
            remove(KEY_ACCESS)
            remove(KEY_REFRESH)
            remove(KEY_USER_ID)
        }
        _accessTokenFlow.value = null
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        prefs.getString(KEY_ACCESS, null)
    }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        prefs.getString(KEY_REFRESH, null)
    }

    suspend fun getUserId(): String = withContext(Dispatchers.IO) {
        prefs.getString(KEY_USER_ID, null).orEmpty()
    }

    // Synchronous — safe to call from non-coroutine context (e.g. startup check)
    fun hasRefreshToken(): Boolean = prefs.getString(KEY_REFRESH, null) != null

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }
}
