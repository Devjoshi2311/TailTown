package com.tailtown.pawcare.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape

private data class Slide(val title: String, val body: String, val heroTint: Color)

private val slides = listOf(
    Slide(
        title = "Care your pet deserves.",
        body = "Book trusted vets, shop daily essentials, and keep every wag and purr healthy.",
        heroTint = CoralSoft,
    ),
    Slide(
        title = "Vets you can trust, near you.",
        body = "Browse verified vets, read reviews, and book same-day or scheduled visits.",
        heroTint = CoralSoft,
    ),
    Slide(
        title = "Everything your pet needs.",
        body = "Shop food, medicine, and accessories — delivered to your door the same day.",
        heroTint = CoralSoft,
    ),
)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bone)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(Modifier.height(32.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val slide = slides[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
            ) {
                // Hero image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(slide.heroTint),
                )

                Spacer(Modifier.height(28.dp))

                // Pagination dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(slides.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(
                                    width = if (index == pagerState.currentPage) 24.dp else 8.dp,
                                    height = 8.dp,
                                )
                                .clip(PillShape)
                                .background(
                                    if (index == pagerState.currentPage) Coral
                                    else Ink900.copy(alpha = 0.18f)
                                ),
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.displayLarge,
                    color = Ink900,
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = slide.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Ink900,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = "Get started",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onSignIn,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink500)) { append("Already have an account? ") }
                    withStyle(SpanStyle(color = Coral, fontWeight = FontWeight.Medium)) {
                        append("Sign in")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    PawcareTheme { WelcomeScreen(onGetStarted = {}, onSignIn = {}) }
}
