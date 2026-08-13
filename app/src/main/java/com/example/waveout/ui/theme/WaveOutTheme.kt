package com.example.waveout.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Dark palette
private val CyanPrimary = Color(0xFF00D9FF)
private val CyanSecondary = Color(0xFF0090B3)
private val MintTertiary = Color(0xFF4DFFB4)
private val DarkBackground = Color(0xFF080C10)
private val DarkSurface = Color(0xFF0D1117)
private val DarkSurfaceVariant = Color(0xFF161B22)
private val DarkOnBackground = Color(0xFFE8EEF4)
private val DarkOnSurface = Color(0xFFD0D9E3)
private val DarkOutline = Color(0xFF30363D)

// Light palette
private val BluePrimary = Color(0xFF0077B6)
private val BlueSecondary = Color(0xFF023E8A)
private val BlueTertiary = Color(0xFF00B4D8)
private val LightBackground = Color(0xFFF0F4F8)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFE2EAF3)
private val LightOnBackground = Color(0xFF0D1117)
private val LightOutline = Color(0xFFB0BEC5)

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF003040),
    primaryContainer = Color(0xFF004D60),
    onPrimaryContainer = Color(0xFFB3F0FF),
    secondary = CyanSecondary,
    onSecondary = Color.White,
    tertiary = MintTertiary,
    onTertiary = Color(0xFF003020),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = DarkOutline,
    outlineVariant = Color(0xFF21262D)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCAE8FF),
    onPrimaryContainer = Color(0xFF001E2E),
    secondary = BlueSecondary,
    onSecondary = Color.White,
    tertiary = BlueTertiary,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF445566),
    outline = LightOutline,
    outlineVariant = Color(0xFFD0DCE8)
)

@Composable
fun WaveOutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = WaveOutTypography,
        content = content
    )
}
