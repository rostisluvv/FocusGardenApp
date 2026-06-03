package com.example.focusgarden.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.Lime
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.ui.theme.focusGardenVisuals

@Composable
fun FocusBackground(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val visuals = focusGardenVisuals()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    visuals.backgroundGradient
                )
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(visuals.backgroundAuraPrimary, Color.Transparent),
                        center = Offset(size.width * 0.03f, size.height * 0.08f),
                        radius = size.width * 0.92f
                    ),
                    radius = size.width * 0.92f,
                    center = Offset(size.width * 0.03f, size.height * 0.08f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(visuals.backgroundAuraSecondary, Color.Transparent),
                        center = Offset(size.width * 1.02f, size.height * 0.35f),
                        radius = size.width * 0.62f
                    ),
                    radius = size.width * 0.62f,
                    center = Offset(size.width * 1.02f, size.height * 0.35f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(visuals.backgroundAuraTertiary, Color.Transparent),
                        center = Offset(size.width * 0.48f, size.height * 1.03f),
                        radius = size.width * 0.86f
                    ),
                    radius = size.width * 0.86f,
                    center = Offset(size.width * 0.48f, size.height * 1.03f)
                )

                val wave = Path().apply {
                    moveTo(-size.width * 0.12f, size.height * 0.18f)
                    cubicTo(
                        size.width * 0.18f, size.height * 0.08f,
                        size.width * 0.40f, size.height * 0.37f,
                        size.width * 0.72f, size.height * 0.22f
                    )
                    cubicTo(
                        size.width * 0.95f, size.height * 0.12f,
                        size.width * 1.05f, size.height * 0.30f,
                        size.width * 1.18f, size.height * 0.18f
                    )
                    lineTo(size.width * 1.18f, size.height * 0.45f)
                    cubicTo(
                        size.width * 0.82f, size.height * 0.58f,
                        size.width * 0.45f, size.height * 0.26f,
                        -size.width * 0.12f, size.height * 0.48f
                    )
                    close()
                }
                drawPath(
                    path = wave,
                    brush = Brush.linearGradient(
                        visuals.backgroundWave
                    )
                )

                val step = 28f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = visuals.backgroundGridLine,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = visuals.backgroundGridLineSoft,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            }
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val visuals = focusGardenVisuals()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    visuals.glassBorder
                ),
                shape = RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = visuals.glassCardContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
fun AccentCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Forest.copy(alpha = 0.94f),
                            Mint.copy(alpha = 0.82f),
                            Lime.copy(alpha = 0.78f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun Modifier.softGlow(): Modifier = this.graphicsLayer {
    shadowElevation = 28f
    shape = RoundedCornerShape(34.dp)
    clip = false
}
