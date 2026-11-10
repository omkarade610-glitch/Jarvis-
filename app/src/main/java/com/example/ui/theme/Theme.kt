package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val JarvisDarkColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004D54),
    onPrimaryContainer = JarvisCyanLight,
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004E57),
    onSecondaryContainer = Color(0xFF80DEEA),
    tertiary = JarvisAmber,
    onTertiary = Color(0xFF3E2E00),
    background = JarvisDeepNavy,
    onBackground = TextPrimary,
    surface = JarvisSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = JarvisCardDark,
    onSurfaceVariant = TextSecondary,
    outline = JarvisBorderCyan,
    error = JarvisRedAlert,
    onError = Color.White
)

private val JarvisLightColorScheme = lightColorScheme(
    primary = JarvisCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF00363A),
    secondary = Color(0xFF00838F),
    onSecondary = Color.White,
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF0A1128),
    surface = Color.White,
    onSurface = Color(0xFF0A1128),
    surfaceVariant = Color(0xFFE0E7FF),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF90CAF9),
    error = JarvisRedAlert,
    onError = Color.White
)

@Composable
fun JarvisTheme(
    darkTheme: Boolean = true, // Default to futuristic dark mode
    dynamicColor: Boolean = false, // Emphasize futuristic neon cyan palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> JarvisDarkColorScheme
        else -> JarvisLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
