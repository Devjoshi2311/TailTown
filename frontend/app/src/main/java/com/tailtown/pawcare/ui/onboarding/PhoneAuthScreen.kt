package com.tailtown.pawcare.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tailtown.pawcare.auth.AuthUiState
import com.tailtown.pawcare.auth.AuthViewModel
import com.tailtown.pawcare.ui.theme.*

@Composable
fun PhoneAuthScreen(
    authViewModel: AuthViewModel,
    onSignedIn: (isNewUser: Boolean) -> Unit,
    // kept for NavGraph compatibility — no longer used
    onCodeSent: (String) -> Unit = {},
    onContinueWithGoogle: () -> Unit = {},
) {
    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is AuthUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.SignedIn) {
            authViewModel.resetState()
            onSignedIn((uiState as AuthUiState.SignedIn).isNewUser)
        }
    }

    LaunchedEffect(email, password) { authViewModel.clearError() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = if (isRegister) "Create account" else "Welcome back.",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isRegister) "Sign up to manage your pet's care." else "Sign in to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink500,
        )

        Spacer(Modifier.height(32.dp))

        // Name field (register only)
        AnimatedVisibility(visible = isRegister) {
            Column {
                FieldLabel("NAME")
                Spacer(Modifier.height(8.dp))
                AuthTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Your full name",
                    enabled = !isLoading,
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        FieldLabel("EMAIL")
        Spacer(Modifier.height(8.dp))
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email,
            enabled = !isLoading,
            isError = uiState is AuthUiState.Error,
        )

        Spacer(Modifier.height(16.dp))

        FieldLabel("PASSWORD")
        Spacer(Modifier.height(8.dp))
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Min. 8 characters",
            keyboardType = KeyboardType.Password,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            enabled = !isLoading,
            isError = uiState is AuthUiState.Error,
            trailingContent = {
                TextButton(onClick = { showPassword = !showPassword }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text(
                        text = if (showPassword) "Hide" else "Show",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )
                }
            },
        )

        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = (uiState as AuthUiState.Error).message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.weight(1f))

        val canSubmit = email.isNotBlank() && password.length >= 8 &&
                (!isRegister || name.isNotBlank()) && !isLoading

        Button(
            onClick = {
                if (isRegister) authViewModel.registerWithEmail(email.trim(), password, name.trim())
                else authViewModel.loginWithEmail(email.trim(), password)
            },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Coral,
                contentColor = Color.White,
                disabledContainerColor = CoralSoft,
                disabledContentColor = Coral.copy(alpha = 0.6f),
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (isRegister) "Create account" else "Sign in",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = {
                isRegister = !isRegister
                authViewModel.clearError()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text(
                text = if (isRegister) "Already have an account? Sign in" else "New here? Create an account",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink900,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelSmall, color = Ink500)
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    isError: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        singleLine = true,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink900),
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (isError) MaterialTheme.colorScheme.error else Hairline,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    )
                    .background(White, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = Ink500.copy(alpha = 0.4f))
                    }
                    inner()
                }
                trailingContent?.invoke()
            }
        },
    )
}

