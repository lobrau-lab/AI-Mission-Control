package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MissionControlColorScheme = darkColorScheme(
    primary = SleekPurple,
    onPrimary = SleekPurpleContainer,
    primaryContainer = SleekCardActive,
    onPrimaryContainer = SleekPurple,
    secondary = SleekGreen,
    onSecondary = SleekBg,
    tertiary = SleekAmber,
    onTertiary = SleekBg,
    background = SleekBg,
    onBackground = TextPrimary,
    surface = SleekCard,
    onSurface = TextPrimary,
    surfaceVariant = SleekCardBorder,
    onSurfaceVariant = TextSecondary,
    error = SleekRed,
    onError = TextPrimary
)

@Composable
fun MissionControlTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MissionControlColorScheme,
        typography = Typography,
        content = content
    )
}

