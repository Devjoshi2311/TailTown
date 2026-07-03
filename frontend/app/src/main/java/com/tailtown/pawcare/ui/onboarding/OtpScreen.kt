package com.tailtown.pawcare.ui.onboarding

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tailtown.pawcare.auth.AuthUiState
import com.tailtown.pawcare.auth.AuthViewModel
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun OtpScreen(
    phone: String,
    authViewModel: AuthViewModel,
    onNewUser: () -> Unit,
    onReturningUser: () -> Unit,
) {
    var otp by remember { mutableStateOf("") }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is AuthUiState.Loading
    val context = LocalContext.current

    // Route based on whether this is a new or returning user
    LaunchedEffect(uiState) {
        when (val s = uiState) {
            is AuthUiState.SignedIn -> {
                authViewModel.resetState()
                if (s.isNewUser) onNewUser() else onReturningUser()
            }
            else -> {}
        }
    }

    // Clear error when user edits OTP
    LaunchedEffect(otp) { authViewModel.clearError() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Step 2 of 3",
            style = MaterialTheme.typography.labelSmall,
            color = Ink500,
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Enter the\n6-digit code.",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sent to +91 $phone",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )

        Spacer(Modifier.height(40.dp))

        // 6-box OTP input driven by hidden BasicTextField
        BasicTextField(
            value = otp,
            onValueChange = { raw ->
                val digits = raw.filter(Char::isDigit)
                if (digits.length <= 6) otp = digits
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            enabled = !isLoading,
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(6) { index ->
                        val char = otp.getOrNull(index)
                        val isCurrent = index == otp.length
                        val hasError = uiState is AuthUiState.Error
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(
                                    width = if (isCurrent || hasError) 1.5.dp else 1.dp,
                                    color = when {
                                        hasError -> MaterialTheme.colorScheme.error
                                        isCurrent -> Coral
                                        else -> Hairline
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .background(White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = char?.toString() ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Ink900,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            },
        )

        Spacer(Modifier.height(10.dp))

        // Error message
        if (uiState is AuthUiState.Error) {
            Text(
                text = (uiState as AuthUiState.Error).message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                otp = ""
                authViewModel.resendOtp(phone, context as Activity)
            },
            enabled = !isLoading,
        ) {
            Text(
                text = "Resend code",
                style = MaterialTheme.typography.bodyMedium,
                color = Coral,
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { authViewModel.verifyOtp(otp) },
            enabled = otp.length == 6 && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Coral,
                contentColor = Color.White,
                disabledContainerColor = Coral.copy(alpha = 0.3f),
                disabledContentColor = White.copy(alpha = 0.6f),
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Verify",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

