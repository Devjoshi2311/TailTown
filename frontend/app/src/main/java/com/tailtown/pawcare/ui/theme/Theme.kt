package com.tailtown.pawcare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PawcareColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = White,
    primaryContainer = CoralSoft,
    onPrimaryContainer = Coral,
    secondary = Ink900,
    onSecondary = White,
    background = Bone,
    onBackground = Ink900,
    surface = White,
    onSurface = Ink900,
    onSurfaceVariant = Ink500,
    outline = Hairline,
    error = Amber600,
)

@Composable
fun PawcareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PawcareColorScheme,
        typography = PawcareTypography,
        shapes = PawcareShapes,
        content = content,
    )
}
