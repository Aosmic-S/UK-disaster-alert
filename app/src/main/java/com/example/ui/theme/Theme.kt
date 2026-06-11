package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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

private val SystemDarkColorScheme = darkColorScheme()
private val SystemLightColorScheme = lightColorScheme()

@Composable
fun MyApplicationTheme(
    themeMode: String = "Immersive",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        "Immersive" -> DarkColorScheme
        else -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) SystemDarkColorScheme else SystemLightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
