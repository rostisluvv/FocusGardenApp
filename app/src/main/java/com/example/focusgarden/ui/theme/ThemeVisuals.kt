package com.example.focusgarden.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

data class FocusGardenVisuals(
    val backgroundGradient: List<Color>,
    val backgroundAuraPrimary: Color,
    val backgroundAuraSecondary: Color,
    val backgroundAuraTertiary: Color,
    val backgroundWave: List<Color>,
    val backgroundGridLine: Color,
    val backgroundGridLineSoft: Color,
    val glassCardContainer: Color,
    val glassBorder: List<Color>,
    val bottomBarContainer: Color,
    val navigationSelectedContainer: Color,
    val navigationUnselected: Color,
    val sheetContainer: Color,
    val chipContainer: Color,
    val selectedChipContainer: Color,
    val iconButtonContainer: Color,
    val iconButtonContent: Color,
    val chartTrack: Color,
    val subtleLine: Color,
    val accent: Color,
    val primaryText: Color,
    val mutedText: Color,
    val disabledButtonGradient: List<Color>
)

object FocusGardenThemeTokens {
    fun visuals(colorScheme: ColorScheme): FocusGardenVisuals {
        val dark = colorScheme.background.luminance() < 0.50f
        return if (dark) darkVisuals() else lightVisuals(colorScheme)
    }

    private fun darkVisuals(): FocusGardenVisuals = FocusGardenVisuals(
        backgroundGradient = listOf(
            Color(0xFF020604),
            Night,
            Color(0xFF071914),
            Color(0xFF08100F)
        ),
        backgroundAuraPrimary = Forest.copy(alpha = 0.26f),
        backgroundAuraSecondary = Mint.copy(alpha = 0.16f),
        backgroundAuraTertiary = Pollen.copy(alpha = 0.10f),
        backgroundWave = listOf(
            Color.White.copy(alpha = 0.035f),
            Lime.copy(alpha = 0.060f),
            Color.Transparent
        ),
        backgroundGridLine = Color.White.copy(alpha = 0.018f),
        backgroundGridLineSoft = Color.White.copy(alpha = 0.012f),
        glassCardContainer = Soil.copy(alpha = 0.70f),
        glassBorder = listOf(
            Color.White.copy(alpha = 0.18f),
            Mint.copy(alpha = 0.07f),
            Color.White.copy(alpha = 0.035f)
        ),
        bottomBarContainer = Night2.copy(alpha = 0.90f),
        navigationSelectedContainer = Forest.copy(alpha = 0.18f),
        navigationUnselected = Color(0xFF6F7A73),
        sheetContainer = Color(0xFF08130F),
        chipContainer = Night2.copy(alpha = 0.72f),
        selectedChipContainer = Forest.copy(alpha = 0.24f),
        iconButtonContainer = Color.White.copy(alpha = 0.075f),
        iconButtonContent = Color.White.copy(alpha = 0.78f),
        chartTrack = Color.White.copy(alpha = 0.060f),
        subtleLine = Color.White.copy(alpha = 0.075f),
        accent = Lime,
        primaryText = Color(0xFFF2FFF7),
        mutedText = TextMuted,
        disabledButtonGradient = listOf(Color(0xFF203129), Color(0xFF203129))
    )

    private fun lightVisuals(colorScheme: ColorScheme): FocusGardenVisuals = FocusGardenVisuals(
        backgroundGradient = listOf(
            Color(0xFFFEFFF9),
            Color(0xFFF7FCF3),
            Color(0xFFEEF7EE),
            Color(0xFFFAFDF8)
        ),
        backgroundAuraPrimary = colorScheme.primary.copy(alpha = 0.10f),
        backgroundAuraSecondary = colorScheme.secondary.copy(alpha = 0.10f),
        backgroundAuraTertiary = colorScheme.tertiary.copy(alpha = 0.13f),
        backgroundWave = listOf(
            Color.White.copy(alpha = 0.56f),
            colorScheme.primaryContainer.copy(alpha = 0.46f),
            Color.Transparent
        ),
        backgroundGridLine = colorScheme.primary.copy(alpha = 0.050f),
        backgroundGridLineSoft = colorScheme.secondary.copy(alpha = 0.036f),
        glassCardContainer = colorScheme.surface.copy(alpha = 0.90f),
        glassBorder = listOf(
            colorScheme.primary.copy(alpha = 0.22f),
            Color.White.copy(alpha = 0.72f),
            colorScheme.secondary.copy(alpha = 0.14f)
        ),
        bottomBarContainer = colorScheme.surface.copy(alpha = 0.96f),
        navigationSelectedContainer = colorScheme.primaryContainer.copy(alpha = 0.92f),
        navigationUnselected = colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
        sheetContainer = colorScheme.surface,
        chipContainer = colorScheme.surfaceVariant.copy(alpha = 0.80f),
        selectedChipContainer = colorScheme.primaryContainer.copy(alpha = 0.92f),
        iconButtonContainer = colorScheme.surfaceVariant.copy(alpha = 0.80f),
        iconButtonContent = colorScheme.onSurface.copy(alpha = 0.78f),
        chartTrack = colorScheme.primary.copy(alpha = 0.10f),
        subtleLine = colorScheme.primary.copy(alpha = 0.14f),
        accent = colorScheme.primary,
        primaryText = colorScheme.onBackground,
        mutedText = colorScheme.onSurfaceVariant,
        disabledButtonGradient = listOf(colorScheme.surfaceVariant, colorScheme.surfaceVariant)
    )
}

@Composable
fun focusGardenVisuals(): FocusGardenVisuals = FocusGardenThemeTokens.visuals(MaterialTheme.colorScheme)
