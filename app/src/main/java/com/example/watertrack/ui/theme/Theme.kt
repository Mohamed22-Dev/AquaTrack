package com.example.watertrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    secondary = GreenAccent,
    background = White,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    error = RedError
)

private val DarkColors = darkColorScheme(
    primary = BluePrimary,
    onPrimary = White,
    secondary = GreenAccent,
    background = Color(0xFF0E1726),
    onBackground = Color(0xFFEAF2FF),
    surface = Color(0xFF16233A),
    onSurface = Color(0xFFEAF2FF),
    error = RedError
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp)
)

@Composable
fun WaterTrackTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}