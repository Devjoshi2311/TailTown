package com.tailtown.pawcare.ui.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun WeightTrackerScreen(
    weightPoints: List<WeightPoint> = sampleWeightPoints,
    petName: String = "your pet",
    petBreed: String = "",
    onBack: () -> Unit,
    onLogWeight: () -> Unit = {},
) {
    val currentWeight = weightPoints.lastOrNull()?.value ?: 28.4f
    val currentWeightStr = "%.1f".format(currentWeight)

    Scaffold(
        containerColor = Bone,
        topBar = {
            WeightTopBar(onBack = onBack)
        },
        bottomBar = {
            WeightBottomBar(onLogWeight = onLogWeight)
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = innerPadding.calculateTopPadding() + 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── 1. Weight header card ─────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                ) {
                    Text(
                        text = "Current weight",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = currentWeightStr,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Ink900,
                        )
                        Text(
                            text = "kg",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Ink500,
                        )
                    }

                    Text(
                        text = "+0.4 kg this month · Healthy",
                        style = MaterialTheme.typography.labelSmall,
                        color = Teal600,
                    )

                    Spacer(Modifier.height(24.dp))

                    WeightLineChart(points = weightPoints)
                }
            }

            // ── 2. Healthy range card ─────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                ) {
                    Text(
                        text = if (petBreed.isNotBlank()) "HEALTHY RANGE FOR ${petBreed.uppercase()}S" else "HEALTHY RANGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink500,
                    )

                    Spacer(Modifier.height(16.dp))

                    HealthyRangeBar(
                        currentWeight = currentWeight,
                        minWeight = 25f,
                        maxWeight = 32f,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "25 kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink500,
                        )
                        Text(
                            text = "32 kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink500,
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "$petName is right where they should be",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Teal600,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightLineChart(points: List<WeightPoint>) {
    val yMin = 27.4f
    val yMax = 28.8f

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val count = points.size
            if (count < 2) return@Canvas

            // Compute x/y pixel positions for each point
            val xStep = canvasWidth / (count - 1).toFloat()
            fun xPos(i: Int) = i * xStep
            fun yPos(v: Float) =
                canvasHeight * (1f - (v - yMin) / (yMax - yMin))

            // Build the fill path (area under line)
            val fillPath = Path().apply {
                moveTo(xPos(0), yPos(points[0].value))
                for (i in 1 until count) {
                    lineTo(xPos(i), yPos(points[i].value))
                }
                lineTo(xPos(count - 1), canvasHeight)
                lineTo(xPos(0), canvasHeight)
                close()
            }

            // Draw CoralSoft fill
            drawPath(
                path = fillPath,
                color = CoralSoft,
                style = Fill,
            )

            // Build the line path
            val linePath = Path().apply {
                moveTo(xPos(0), yPos(points[0].value))
                for (i in 1 until count) {
                    lineTo(xPos(i), yPos(points[i].value))
                }
            }

            // Draw Coral line
            drawPath(
                path = linePath,
                color = Coral,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )

            // Draw coral filled circle at last point
            val lastX = xPos(count - 1)
            val lastY = yPos(points[count - 1].value)
            drawCircle(
                color = Coral,
                radius = 5.dp.toPx(),
                center = Offset(lastX, lastY),
            )
            // White inner circle
            drawCircle(
                color = White,
                radius = 3.dp.toPx(),
                center = Offset(lastX, lastY),
            )
        }

        // Month labels below chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            points.forEach { point ->
                Text(
                    text = point.monthLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink500,
                )
            }
        }
    }
}

@Composable
private fun HealthyRangeBar(
    currentWeight: Float,
    minWeight: Float,
    maxWeight: Float,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
    ) {
        val canvasWidth = size.width
        val trackHeight = 6.dp.toPx()
        val trackTop = 12.dp.toPx() - trackHeight / 2f
        val cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())

        // Full-width gray track
        drawRoundRect(
            color = Hairline,
            topLeft = Offset(0f, trackTop),
            size = Size(canvasWidth, trackHeight),
            cornerRadius = cornerRadius,
        )

        // Teal fill to ~70% width
        drawRoundRect(
            color = Teal600.copy(alpha = 0.45f),
            topLeft = Offset(0f, trackTop),
            size = Size(canvasWidth * 0.70f, trackHeight),
            cornerRadius = cornerRadius,
        )

        // Indicator circle at current weight position
        val fraction = (currentWeight - minWeight) / (maxWeight - minWeight)
        val indicatorX = fraction * canvasWidth
        val indicatorY = 12.dp.toPx()

        // Black outer circle
        drawCircle(
            color = Ink900,
            radius = 8.dp.toPx(),
            center = Offset(indicatorX, indicatorY),
        )
        // White inner circle
        drawCircle(
            color = White,
            radius = 5.dp.toPx(),
            center = Offset(indicatorX, indicatorY),
        )
    }
}

@Composable
private fun WeightTopBar(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Ink900,
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Weight",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink900,
            )
        }
        HorizontalDivider(color = Hairline)
    }
}

@Composable
private fun WeightBottomBar(onLogWeight: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Hairline)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            Button(
                onClick = onLogWeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink900,
                    contentColor = White,
                ),
            ) {
                Text(
                    text = "Log weight",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightTrackerScreenPreview() {
    PawcareTheme {
        WeightTrackerScreen(onBack = {})
    }
}
