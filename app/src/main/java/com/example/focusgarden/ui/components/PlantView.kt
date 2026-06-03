package com.example.focusgarden.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.ui.theme.Pollen
import com.example.focusgarden.ui.theme.focusGardenVisuals

@Composable
fun PlantView(
    stage: PlantStage,
    progress: Float,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false,
    size: Int = 238
) {
    val transition = rememberInfiniteTransition(label = "plant_breath")
    val breath by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plant_scale"
    )

    val errorColor = MaterialTheme.colorScheme.error
    val visuals = focusGardenVisuals()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = this.size.minDimension * 0.48f
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Mint.copy(alpha = 0.32f), Forest.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    radius = radius,
                    center = center
                )
                repeat(4) { index ->
                    drawCircle(
                        color = if (stage == PlantStage.WITHERED_TREE) errorColor.copy(alpha = 0.055f) else visuals.accent.copy(alpha = 0.05f - index * 0.006f),
                        radius = radius * (0.72f + index * 0.10f),
                        center = center,
                        style = Stroke(width = 1.4f)
                    )
                }
                val top = Offset(center.x, center.y - radius * 0.96f)
                val bottom = Offset(center.x, center.y + radius * 0.96f)
                drawLine(
                    color = Pollen.copy(alpha = 0.10f),
                    start = top,
                    end = bottom,
                    strokeWidth = 1.2f
                )
                drawLine(
                    color = Pollen.copy(alpha = 0.055f),
                    start = Offset(center.x - radius * 0.62f, center.y - radius * 0.70f),
                    end = Offset(center.x + radius * 0.62f, center.y + radius * 0.70f),
                    strokeWidth = 1.0f
                )
            }

            Box(
                modifier = Modifier
                    .size((size - 20).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.055f), Color.Transparent)
                        )
                    )
            )

            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.size((size - 28).dp),
                strokeWidth = 10.dp,
                color = if (stage == PlantStage.WITHERED_TREE) MaterialTheme.colorScheme.error else visuals.accent,
                trackColor = visuals.chartTrack
            )

            FocusPlantGlyph(
                stage = stage,
                modifier = Modifier
                    .size((size * 0.54f).dp)
                    .graphicsLayer {
                        scaleX = breath
                        scaleY = breath
                    }
            )
        }
        if (showLabel) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stage.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.84f)
            )
        }
    }
}
