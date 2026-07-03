package com.tailtown.pawcare.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

private enum class Species(val label: String, val emoji: String) {
    Dog("Dog", "🐶"),
    Cat("Cat", "🐱"),
    Bird("Bird", "🐦"),
}

@Composable
fun AddPetScreen(
    onAddPet: (name: String, breed: String, age: String, species: String, weight: String) -> Unit,
    onSkip: () -> Unit,
) {
    var species by remember { mutableStateOf(Species.Dog) }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Coral,
        unfocusedBorderColor = Hairline,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        focusedTextColor = Ink900,
        unfocusedTextColor = Ink900,
        cursorColor = Coral,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Step 3 of 3",
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Coral,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Tell us about\nyour pet.",
            style = MaterialTheme.typography.displayLarge,
            color = Ink900,
        )

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // Species selector
            Text(
                text = "SPECIES",
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Species.entries.forEach { s ->
                    val selected = s == species
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) Coral else Hairline,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .background(White, RoundedCornerShape(12.dp))
                            .clickable { species = s },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = s.emoji, fontSize = 20.sp)
                            Text(
                                text = s.label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                ),
                                color = if (selected) Coral else Ink900,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Name
            Text(
                text = "NAME",
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Bruno", color = Ink500.copy(alpha = 0.45f)) },
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true,
            )

            Spacer(Modifier.height(24.dp))

            // Breed
            Text(
                text = "BREED",
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Labrador", color = Ink500.copy(alpha = 0.45f)) },
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true,
            )

            Spacer(Modifier.height(24.dp))

            // Age & Weight
            Text(
                text = "AGE & WEIGHT",
                style = MaterialTheme.typography.labelSmall,
                color = Ink500,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("3 yr", color = Ink500.copy(alpha = 0.45f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("4.5 kg", color = Ink500.copy(alpha = 0.45f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        Button(
            onClick = { onAddPet(name, breed, age, species.label, weight) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Coral,
                contentColor = White,
            ),
        ) {
            Text(
                text = if (name.isNotBlank()) "Add $name" else "Add pet",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPetScreenPreview() {
    PawcareTheme { AddPetScreen(onAddPet = { _, _, _, _, _ -> }, onSkip = {}) }
}
