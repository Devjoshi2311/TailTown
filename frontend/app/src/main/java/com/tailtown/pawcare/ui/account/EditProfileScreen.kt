package com.tailtown.pawcare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.inbox.sampleAccountUser
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun EditProfileScreen(
    initialName: String = sampleAccountUser.name,
    initialPhone: String = sampleAccountUser.phone,
    initialEmail: String = "riya.sharma@gmail.com",
    onSave: (name: String, phone: String, email: String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit,
) {
    var name  by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var email by remember { mutableStateOf(initialEmail) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .imePadding(),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding(),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                text = "Edit profile",
                style = MaterialTheme.typography.displayLarge,
                color = Ink900,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
            )
            HorizontalDivider(color = Hairline)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            ProfileField(label = "Full name", value = name, onValueChange = { name = it })
            Spacer(Modifier.height(16.dp))
            ProfileField(label = "Phone number", value = phone, onValueChange = { phone = it })
            Spacer(Modifier.height(16.dp))
            ProfileField(label = "Email", value = email, onValueChange = { email = it })
        }

        // ── Save button ───────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Button(
                onClick = { onSave(name, phone, email) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = Ink900, contentColor = White),
            ) {
                Text("Save changes", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Ink500)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Coral,
                unfocusedBorderColor = Hairline,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink900),
            singleLine = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfilePreview() {
    PawcareTheme { EditProfileScreen(onBack = {}) }
}
