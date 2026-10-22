package com.jyotishsutra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Aubergine = Color(0xFF431458)
val DeepPlum = Color(0xFF260D35)
val Saffron = Color(0xFFF28B2E)
val MutedGold = Color(0xFFC78B2D)
val WarmIvory = Color(0xFFFFF8F1)
val PaleGold = Color(0xFFFFECD1)
val Ink = Color(0xFF2D1A31)
val MutedInk = Color(0xFF6D5B6F)

private val LightColors = lightColorScheme(
    primary = Aubergine,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1DAF8),
    onPrimaryContainer = DeepPlum,
    secondary = Saffron,
    onSecondary = Color.White,
    secondaryContainer = PaleGold,
    onSecondaryContainer = Color(0xFF3A2100),
    tertiary = MutedGold,
    background = WarmIvory,
    onBackground = Ink,
    surface = Color(0xFFFFFBF7),
    onSurface = Ink,
    surfaceVariant = Color(0xFFF3EAE2),
    onSurfaceVariant = MutedInk,
    outline = Color(0xFF9B899D),
)

@Composable
fun JyotishSutraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
