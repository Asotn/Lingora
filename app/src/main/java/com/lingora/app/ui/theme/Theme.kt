package com.lingora.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Lingora ships a single, deliberate dark "aurora" theme rather than a
// light/dark switch: the glow effect that defines the app's look is
// designed for a dark canvas, and diluting it with a light variant would
// work against the visual identity the app is built around.
private val LingoraColorScheme = darkColorScheme(
    primary = AuroraTeal,
    secondary = AuroraViolet,
    tertiary = AuroraMagenta,
    background = AuroraNight,
    surface = AuroraNightElevated,
    onPrimary = AuroraNight,
    onSecondary = AuroraNight,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun LingoraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LingoraColorScheme,
        typography = LingoraTypography,
        content = content
    )
}
