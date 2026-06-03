package com.example.focusgarden.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Night = Color(0xFF050B09)
val Night2 = Color(0xFF0A1712)
val Soil = Color(0xFF122219)
val Moss = Color(0xFF193428)
val Forest = Color(0xFF1FE08E)
val Mint = Color(0xFF82FFD0)
val Lime = Color(0xFFCCFF66)
val Pollen = Color(0xFFE7FF9D)
val Clay = Color(0xFFFFB36A)
val Rose = Color(0xFFFF7D99)
val CardDark = Color(0xFF0E1B15)
val CardDark2 = Color(0xFF13291F)
val TextSoft = Color(0xFFC8E7D8)
val TextMuted = Color(0xFF779084)

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF08794D),
    secondary = Color(0xFF10A66A),
    tertiary = Color(0xFF7BCC76),
    background = Color(0xFFF4FAF4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE6F1E8),
    primaryContainer = Color(0xFFD9F8E6),
    secondaryContainer = Color(0xFFDFF5ED),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0B1712),
    onSurface = Color(0xFF0B1712),
    onSurfaceVariant = Color(0xFF50655A)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Forest,
    secondary = Mint,
    tertiary = Lime,
    background = Night,
    surface = CardDark,
    surfaceVariant = CardDark2,
    primaryContainer = Color(0xFF174A35),
    secondaryContainer = Color(0xFF123A2D),
    onPrimary = Color(0xFF03110D),
    onSecondary = Color(0xFF03110D),
    onBackground = Color(0xFFEAFBF3),
    onSurface = Color(0xFFEAFBF3),
    onSurfaceVariant = TextSoft,
    error = Rose
)

fun focusGardenColorScheme(darkTheme: Boolean): ColorScheme = if (darkTheme) DarkColors else LightColors

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

private val FocusTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 62.sp,
        lineHeight = 66.sp,
        letterSpacing = (-3.2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-1.2).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.4).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.4.sp
    )
)

@Composable
fun FocusGardenTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val darkTheme = ThemeResolver.shouldUseDarkTheme(themeMode, isSystemInDarkTheme())
    MaterialTheme(
        colorScheme = focusGardenColorScheme(darkTheme),
        typography = FocusTypography,
        shapes = AppShapes,
        content = content
    )
}
