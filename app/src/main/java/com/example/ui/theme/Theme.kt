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
    primary = AlarmPrimaryDark,
    onPrimary = AlarmOnPrimaryDark,
    primaryContainer = AlarmPrimaryContainerDark,
    onPrimaryContainer = AlarmOnPrimaryContainerDark,
    secondary = AlarmSecondaryDark,
    onSecondary = AlarmOnSecondaryDark,
    secondaryContainer = AlarmSecondaryContainerDark,
    onSecondaryContainer = AlarmOnSecondaryContainerDark,
    background = AlarmBackgroundDark,
    surface = AlarmSurfaceDark,
    surfaceVariant = AlarmSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = AlarmPrimaryLight,
    onPrimary = AlarmOnPrimaryLight,
    primaryContainer = AlarmPrimaryContainerLight,
    onPrimaryContainer = AlarmOnPrimaryContainerLight,
    secondary = AlarmSecondaryLight,
    onSecondary = AlarmOnSecondaryLight,
    secondaryContainer = AlarmSecondaryContainerLight,
    onSecondaryContainer = AlarmOnSecondaryContainerLight,
    background = AlarmBackgroundLight,
    surface = AlarmSurfaceLight,
    surfaceVariant = AlarmSurfaceVariantLight
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep high-energy brand colors for alarm clarity
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
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = Theme(darkTheme, dynamicColor, content)
