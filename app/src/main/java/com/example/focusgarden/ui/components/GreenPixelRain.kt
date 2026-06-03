package com.example.focusgarden.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun GreenPixelRain(
    modifier: Modifier = Modifier,
    intensity: Int = 72
) {
    val drops = remember(intensity) {
        List(intensity) { index ->
            val r = Random(index * 97 + 13)
            PixelDrop(
                x = r.nextFloat(),
                start = r.nextFloat(),
                speed = 0.34f + r.nextFloat() * 0.86f,
                size = 2.0f + r.nextFloat() * 5.4f,
                length = 8f + r.nextFloat() * 32f,
                alpha = 0.06f + r.nextFloat() * 0.34f,
                drift = -0.018f + r.nextFloat() * 0.036f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "green_pixel_rain")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drops.forEach { drop ->
            val yProgress = (drop.start + phase * drop.speed) % 1f
            val x = ((drop.x + phase * drop.drift + 1f) % 1f) * size.width
            val y = yProgress * size.height
            val pulse = 0.62f + 0.38f * kotlin.math.sin((phase * 6.28f + drop.start * 9f).toDouble()).toFloat().coerceIn(-1f, 1f).let { (it + 1f) / 2f }
            val base = if (drop.size > 5f) Color(0xFFCFFF72) else Color(0xFF63FFAB)
            drawRoundRect(
                color = base.copy(alpha = drop.alpha * pulse),
                topLeft = Offset(x, y),
                size = Size(drop.size, drop.length),
                cornerRadius = CornerRadius(drop.size, drop.size)
            )
            drawRoundRect(
                color = Color(0xFF123E2B).copy(alpha = drop.alpha * 0.38f),
                topLeft = Offset(x + drop.size * 0.34f, y - drop.length * 0.52f),
                size = Size(drop.size * 0.58f, drop.length * 0.64f),
                cornerRadius = CornerRadius(drop.size, drop.size)
            )
            if (y < drop.length) {
                drawRoundRect(
                    color = Color(0xFFB9FFD7).copy(alpha = drop.alpha * 0.42f),
                    topLeft = Offset(x, y + size.height),
                    size = Size(drop.size, drop.length),
                    cornerRadius = CornerRadius(drop.size, drop.size)
                )
            }
        }
    }
}

private data class PixelDrop(
    val x: Float,
    val start: Float,
    val speed: Float,
    val size: Float,
    val length: Float,
    val alpha: Float,
    val drift: Float
)
