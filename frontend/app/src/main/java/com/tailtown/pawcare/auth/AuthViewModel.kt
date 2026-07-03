package com.tailtown.pawcare.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.tailtown.pawcare.data.TokenStore
import com.tailtown.pawcare.data.remote.ApiService
import com.tailtown.pawcare.data.remote.dto.FirebaseAuthRequestDto
import com.tailtown.pawcare.data.remote.dto.LoginRequestDto
import com.tailtown.pawcare.data.remote.dto.RegisterRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object OtpSent : AuthUiState()
    data class SignedIn(val isNewUser: Boolean) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // True if Firebase session exists OR backend tokens are stored (covers email/password users)
    val isLoggedIn: Boolean get() = auth.currentUser != null || tokenStore.hasRefreshToken()

    // Emits null when tokens are cleared (expired refresh or explicit sign-out) → navigate to login
    val sessionActiveFlow = tokenStore.accessTokenFlow

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // ── OTP sending ────────────────────────────────────────────────────────

    fun sendOtp(phone: String, activity: Activity) {
        _uiState.value = AuthUiState.Loading
        triggerVerification(phone, activity, resendToken = null)
    }

    fun resendOtp(phone: String, activity: Activity) {
        _uiState.value = AuthUiState.Loading
        triggerVerification(phone, activity, resendToken = resendToken)
    }

    private fun triggerVerification(
        phone: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(buildCallbacks())

        resendToken?.let { builder.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    private fun buildCallbacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // Auto-retrieval or instant verification succeeded
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            viewModelScope.launch { signIn(credential) }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _uiState.value = AuthUiState.Error(
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format"
                    else -> e.message ?: "Verification failed. Try again."
                }
            )
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            storedVerificationId = verificationId
            resendToken = token
            _uiState.value = AuthUiState.OtpSent
        }
    }

    // ── OTP verification ───────────────────────────────────────────────────

    fun verifyOtp(otp: String) {
        val id = storedVerificationId ?: run {
            _uiState.value = AuthUiState.Error("Session expired. Please resend the code.")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            signIn(PhoneAuthProvider.getCredential(id, otp))
        }
    }

    private suspend fun signIn(credential: PhoneAuthCredential) {
        try {
            val result = auth.signInWithCredential(credential).await()
            val isNewUser = result.additionalUserInfo?.isNewUser ?: true
            syncWithBackend(result.user?.uid ?: "", result.user?.phoneNumber ?: "")
            _uiState.value = AuthUiState.SignedIn(isNewUser)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            _uiState.value = AuthUiState.Error("Incorrect code. Try again.")
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error(e.message ?: "Sign-in failed. Try again.")
        }
    }

    private suspend fun syncWithBackend(uid: String, phone: String) {
        try {
            val idToken = auth.currentUser?.getIdToken(false)?.await()?.token ?: return
            val displayName = phone.ifBlank { "User" }
            val response = api.firebaseAuth(FirebaseAuthRequestDto(idToken = idToken, displayName = displayName))
            response.data?.let { authResp ->
                tokenStore.saveTokens(authResp.accessToken, authResp.refreshToken, authResp.user.id)
            }
        } catch (_: Exception) {
            // Backend sync failure is non-fatal — Firebase auth still works
        }
    }

    // ── Email / Password login (no Firebase) ───────────────────────────────

    fun loginWithEmail(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequestDto(email, password))
                response.data?.let { authResp ->
                    tokenStore.saveTokens(authResp.accessToken, authResp.refreshToken, authResp.user.id)
                }
                _uiState.value = AuthUiState.SignedIn(isNewUser = false)
            } catch (e: HttpException) {
                _uiState.value = AuthUiState.Error(
                    when (e.code()) {
                        401, 403 -> "Incorrect email or password."
                        400 -> "Invalid request — check your details and try again."
                        else -> "Server error (${e.code()}). Try again."
                    }
                )
            } catch (e: IOException) {
                _uiState.value = AuthUiState.Error("Cannot reach server. Check that the backend is running and your device is on the same network.")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Unexpected error: ${e.javaClass.simpleName} — ${e.message}")
            }
        }
    }

    fun registerWithEmail(email: String, password: String, name: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val response = api.register(RegisterRequestDto(email, password, name))
                response.data?.let { authResp ->
                    tokenStore.saveTokens(authResp.accessToken, authResp.refreshToken, authResp.user.id)
                }
                _uiState.value = AuthUiState.SignedIn(isNewUser = true)
            } catch (e: HttpException) {
                _uiState.value = AuthUiState.Error(
                    when (e.code()) {
                        409 -> "An account with this email already exists."
                        400 -> "Invalid request — check your details and try again."
                        else -> "Server error (${e.code()}). Try again."
                    }
                )
            } catch (e: IOException) {
                _uiState.value = AuthUiState.Error("Cannot reach server. Check that the backend is running and your device is on the same network.")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Unexpected error: ${e.javaClass.simpleName} — ${e.message}")
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) _uiState.value = AuthUiState.Idle
    }

    fun signOut() {
        auth.signOut()
        viewModelScope.launch { tokenStore.clearTokens() }
        storedVerificationId = null
        resendToken = null
        _uiState.value = AuthUiState.Idle
    }
}
