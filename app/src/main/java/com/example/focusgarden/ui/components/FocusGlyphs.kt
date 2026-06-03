package com.example.focusgarden.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.Lime
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.ui.theme.Rose
import com.example.focusgarden.ui.theme.TextMuted
import com.example.focusgarden.ui.theme.focusGardenVisuals

@Composable
fun CategoryGlyph(
    name: String,
    modifier: Modifier = Modifier.size(28.dp),
    selected: Boolean = false
) {
    val visuals = focusGardenVisuals()
    val color = if (selected) visuals.accent else visuals.mutedText.copy(alpha = 0.92f)
    Canvas(modifier = modifier) {
        drawCategoryGlyph(name = name, color = color)
    }
}

@Composable
fun FocusPlantGlyph(
    stage: PlantStage,
    modifier: Modifier = Modifier.size(96.dp),
    active: Boolean = true
) {
    Canvas(modifier = modifier) {
        drawPlantGlyph(stage = stage, active = active)
    }
}

@Composable
fun SessionStatusGlyph(
    success: Boolean,
    modifier: Modifier = Modifier.size(28.dp)
) {
    val visuals = focusGardenVisuals()
    Canvas(modifier = modifier) {
        val color = if (success) visuals.accent else Rose.copy(alpha = 0.88f)
        val stroke = Stroke(width = size.minDimension * 0.075f, cap = StrokeCap.Round)
        drawCircle(
            color = color.copy(alpha = 0.12f),
            radius = size.minDimension * 0.44f,
            center = center
        )
        drawCircle(
            color = color,
            radius = size.minDimension * 0.34f,
            center = center,
            style = stroke
        )
        if (success) {
            drawLine(color, Offset(size.width * 0.34f, size.height * 0.52f), Offset(size.width * 0.46f, size.height * 0.65f), strokeWidth = size.minDimension * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.46f, size.height * 0.65f), Offset(size.width * 0.70f, size.height * 0.36f), strokeWidth = size.minDimension * 0.075f, cap = StrokeCap.Round)
        } else {
            drawLine(color, Offset(size.width * 0.36f, size.height * 0.36f), Offset(size.width * 0.64f, size.height * 0.64f), strokeWidth = size.minDimension * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.64f, size.height * 0.36f), Offset(size.width * 0.36f, size.height * 0.64f), strokeWidth = size.minDimension * 0.075f, cap = StrokeCap.Round)
        }
    }
}

fun DrawScope.drawCategoryGlyph(name: String, color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = w * 0.075f, cap = StrokeCap.Round)
    val key = name.lowercase()
    when {
        key.contains("тело") || key == "body" -> {
            drawCircle(color = color, radius = w * 0.10f, center = Offset(w * 0.50f, h * 0.24f), style = stroke)
            drawLine(color, Offset(w * 0.50f, h * 0.36f), Offset(w * 0.50f, h * 0.75f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.28f, h * 0.48f), Offset(w * 0.72f, h * 0.48f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.50f, h * 0.75f), Offset(w * 0.34f, h * 0.94f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.50f, h * 0.75f), Offset(w * 0.66f, h * 0.94f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
        }
        key.contains("разум") || key == "mind" -> {
            drawCircle(color = color, radius = w * 0.32f, center = center, style = stroke)
            listOf(
                Offset(w * 0.35f, h * 0.40f),
                Offset(w * 0.63f, h * 0.38f),
                Offset(w * 0.48f, h * 0.64f)
            ).forEach { node -> drawCircle(color = color, radius = w * 0.055f, center = node) }
            drawLine(color, Offset(w * 0.35f, h * 0.40f), Offset(w * 0.63f, h * 0.38f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.35f, h * 0.40f), Offset(w * 0.48f, h * 0.64f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.63f, h * 0.38f), Offset(w * 0.48f, h * 0.64f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
        }
        key.contains("дело") || key == "do" -> {
            drawRoundRect(
                color = color,
                topLeft = Offset(w * 0.24f, h * 0.30f),
                size = Size(w * 0.52f, h * 0.46f),
                style = stroke
            )
            drawLine(color, Offset(w * 0.36f, h * 0.30f), Offset(w * 0.36f, h * 0.20f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.64f, h * 0.30f), Offset(w * 0.64f, h * 0.20f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.38f, h * 0.55f), Offset(w * 0.48f, h * 0.65f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.48f, h * 0.65f), Offset(w * 0.66f, h * 0.43f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
        }
        key.contains("поряд") || key == "order" -> {
            val cell = w * 0.18f
            val startX = w * 0.24f
            val startY = h * 0.24f
            repeat(2) { row ->
                repeat(2) { col ->
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(startX + col * w * 0.28f, startY + row * h * 0.28f),
                        size = Size(cell, cell),
                        style = stroke
                    )
                }
            }
            drawLine(color, Offset(w * 0.25f, h * 0.80f), Offset(w * 0.75f, h * 0.80f), strokeWidth = w * 0.065f, cap = StrokeCap.Round)
        }
        else -> {
            drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.38f, h * 0.38f), style = stroke)
            drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.64f, h * 0.38f), style = stroke)
            drawLine(color, Offset(w * 0.26f, h * 0.78f), Offset(w * 0.50f, h * 0.58f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.50f, h * 0.58f), Offset(w * 0.74f, h * 0.78f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
        }
    }
}

fun DrawScope.drawPlantGlyph(stage: PlantStage, active: Boolean = true) {
    val w = size.width
    val h = size.height
    val base = Offset(w * 0.50f, h * 0.82f)
    val soilColor = TextMuted.copy(alpha = 0.42f)
    val main = if (stage == PlantStage.WITHERED_TREE) Rose.copy(alpha = 0.78f) else if (active) Lime else Forest
    val secondary = if (stage == PlantStage.WITHERED_TREE) Rose.copy(alpha = 0.34f) else Mint.copy(alpha = 0.58f)
    val stemHeight = when (stage) {
        PlantStage.SEED -> h * 0.10f
        PlantStage.SPROUT -> h * 0.24f
        PlantStage.SMALL_TREE -> h * 0.38f
        PlantStage.BIG_TREE -> h * 0.52f
        PlantStage.FULL_TREE -> h * 0.62f
        PlantStage.WITHERED_TREE -> h * 0.42f
    }
    drawLine(soilColor, Offset(w * 0.20f, h * 0.84f), Offset(w * 0.80f, h * 0.84f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)

    if (stage == PlantStage.SEED) {
        drawOval(
            color = main.copy(alpha = 0.86f),
            topLeft = Offset(w * 0.39f, h * 0.67f),
            size = Size(w * 0.22f, h * 0.13f)
        )
        drawCircle(color = main.copy(alpha = 0.12f), radius = w * 0.30f, center = base)
        return
    }

    val top = Offset(w * if (stage == PlantStage.WITHERED_TREE) 0.42f else 0.50f, h * 0.82f - stemHeight)
    drawLine(main, base, top, strokeWidth = w * 0.055f, cap = StrokeCap.Round)

    fun leaf(cx: Float, cy: Float, sx: Float, sy: Float, alpha: Float = 1f) {
        drawOval(
            color = secondary.copy(alpha = alpha),
            topLeft = Offset(cx - sx / 2f, cy - sy / 2f),
            size = Size(sx, sy)
        )
    }

    if (stage == PlantStage.WITHERED_TREE) {
        drawLine(Rose.copy(alpha = 0.60f), top, Offset(w * 0.34f, h * 0.40f), strokeWidth = w * 0.036f, cap = StrokeCap.Round)
        drawLine(Rose.copy(alpha = 0.46f), top, Offset(w * 0.58f, h * 0.48f), strokeWidth = w * 0.036f, cap = StrokeCap.Round)
        leaf(w * 0.34f, h * 0.88f, w * 0.16f, h * 0.055f, 0.42f)
        return
    }

    when (stage) {
        PlantStage.SPROUT -> {
            leaf(w * 0.41f, h * 0.60f, w * 0.25f, h * 0.12f, 0.86f)
            leaf(w * 0.59f, h * 0.58f, w * 0.25f, h * 0.12f, 0.86f)
        }
        PlantStage.SMALL_TREE -> {
            leaf(w * 0.38f, h * 0.55f, w * 0.26f, h * 0.12f, 0.82f)
            leaf(w * 0.62f, h * 0.49f, w * 0.28f, h * 0.13f, 0.88f)
            leaf(w * 0.50f, h * 0.38f, w * 0.22f, h * 0.11f, 0.92f)
        }
        PlantStage.BIG_TREE, PlantStage.FULL_TREE -> {
            drawCircle(color = secondary.copy(alpha = 0.22f), radius = w * 0.30f, center = Offset(w * 0.50f, h * 0.43f))
            leaf(w * 0.34f, h * 0.50f, w * 0.28f, h * 0.13f, 0.78f)
            leaf(w * 0.66f, h * 0.50f, w * 0.28f, h * 0.13f, 0.78f)
            leaf(w * 0.42f, h * 0.35f, w * 0.28f, h * 0.13f, 0.88f)
            leaf(w * 0.58f, h * 0.34f, w * 0.28f, h * 0.13f, 0.88f)
            drawCircle(color = main.copy(alpha = 0.70f), radius = w * (if (stage == PlantStage.FULL_TREE) 0.11f else 0.08f), center = Offset(w * 0.50f, h * 0.24f))
        }
        else -> Unit
    }
}
