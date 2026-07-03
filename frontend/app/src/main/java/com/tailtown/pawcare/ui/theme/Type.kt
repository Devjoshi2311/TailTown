package com.tailtown.pawcare.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Replace FontFamily.Serif with FontFamily(Font(R.font.fraunces)) once you
// add the Fraunces TTF to res/font/. Inter is already the default sans-serif
// on most Android devices so no change needed there.
private val FrauncesFamily = FontFamily.Serif

val PawcareTypography = Typography(
    // Display — Fraunces 500 — marketing headings
    displayLarge = TextStyle(
        fontFamily = FrauncesFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp,
        lineHeight = 40.sp,
    ),
    // Heading — Inter 500 — screen titles (22/28)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    // Body — Inter 400 — default body copy (14/22)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    // Eyebrow — Inter Caps 500 — section labels / tags
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.sp,
    ),
)
