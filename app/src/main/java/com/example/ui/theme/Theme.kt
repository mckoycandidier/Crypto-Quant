package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuantColorScheme = darkColorScheme(
    primary = QuantGreen,
    onPrimary = Color.Black,
    primaryContainer = QuantGreenDark,
    onPrimaryContainer = QuantGreenGlow,
    secondary = QuantCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = QuantAmber,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFDE68A),
    background = QuantDarkBackground,
    onBackground = QuantTextPrimary,
    surface = QuantDarkSurface,
    onSurface = QuantTextPrimary,
    surfaceVariant = QuantDarkSurfaceVariant,
    onSurfaceVariant = QuantTextSecondary,
    error = QuantRed,
    onError = Color.White,
    errorContainer = QuantRedDark,
    onErrorContainer = QuantRedGlow,
    outline = QuantBorder,
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuantColorScheme,
        typography = Typography,
        content = content
    )
}
