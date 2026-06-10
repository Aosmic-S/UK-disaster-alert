package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ImmersivePrimary,
    secondary = ImmersiveSecondary,
    background = ImmersiveBackground,
    surface = ImmersiveSurface,
    surfaceVariant = ImmersiveSurfaceVariant,
    onBackground = ImmersiveOnBackground,
    onSurface = ImmersiveOnBackground,
    onSurfaceVariant = ImmersiveMutedText,
    error = ImmersiveError,
    errorContainer = ImmersiveErrorContainer,
    onErrorContainer = ImmersiveError
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
