package com.nebula.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NebulaDark = Color(0xFF1A1230)
val NebulaDeep = Color(0xFF241542)
val NebulaPurple = Color(0xFF7C4DFF)
val NebulaViolet = Color(0xFFB388FF)
val NebulaPink = Color(0xFFFF80AB)
val NebulaCyan = Color(0xFF84FFFF)
val NebulaAmber = Color(0xFFFFD54F)
val NebulaBackground = Color(0xFFF7F4FF)
val NebulaSurface = Color(0xFFFFFFFF)
val NebulaOnDark = Color(0xFFEDE7F6)

private val LightColors = lightColorScheme(
    primary = NebulaPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E0FF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = NebulaViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0E0FF),
    onSecondaryContainer = Color(0xFF2B0B55),
    tertiary = NebulaPink,
    onTertiary = Color.White,
    background = NebulaBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = NebulaSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

private val DarkColors = darkColorScheme(
    primary = NebulaViolet,
    onPrimary = Color(0xFF361E72),
    primaryContainer = Color(0xFF4B2A9E),
    onPrimaryContainer = Color(0xFFE9E0FF),
    secondary = NebulaPink,
    onSecondary = Color(0xFF4A0F3B),
    secondaryContainer = Color(0xFF632B52),
    onSecondaryContainer = Color(0xFFFFD9EC),
    tertiary = NebulaCyan,
    onTertiary = Color(0xFF00363A),
    background = NebulaDark,
    onBackground = NebulaOnDark,
    surface = NebulaDeep,
    onSurface = NebulaOnDark,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Composable
fun NebulaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}