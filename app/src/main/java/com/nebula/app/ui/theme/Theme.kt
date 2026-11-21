package com.nebula.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nebula.app.R

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

val NebulaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

fun fontFamilyFor(selection: String): FontFamily = when (selection) {
    "inter" -> FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_bold, FontWeight.Bold)
    )
    "montserrat" -> FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_semibold, FontWeight.SemiBold),
        Font(R.font.montserrat_bold, FontWeight.Bold)
    )
    "playfair" -> FontFamily(
        Font(R.font.playfair_regular, FontWeight.Normal),
        Font(R.font.playfair_italic, FontWeight.Normal, style = androidx.compose.ui.text.font.FontStyle.Italic),
        Font(R.font.playfair_semibold, FontWeight.SemiBold),
        Font(R.font.playfair_bold, FontWeight.Bold)
    )
    "comfortaa" -> FontFamily(
        Font(R.font.comfortaa_regular, FontWeight.Normal),
        Font(R.font.comfortaa_medium, FontWeight.Medium),
        Font(R.font.comfortaa_bold, FontWeight.Bold)
    )
    "source" -> FontFamily(
        Font(R.font.source_regular, FontWeight.Normal),
        Font(R.font.source_medium, FontWeight.Medium),
        Font(R.font.source_semibold, FontWeight.SemiBold),
        Font(R.font.source_bold, FontWeight.Bold)
    )
    else -> FontFamily.Default
}

private fun Typography.withFamily(family: FontFamily): Typography {
    if (family == FontFamily.Default) return this
    fun apply(style: androidx.compose.ui.text.TextStyle) = style.copy(fontFamily = family)
    return copy(
        displayLarge = apply(displayLarge),
        displayMedium = apply(displayMedium),
        displaySmall = apply(displaySmall),
        headlineLarge = apply(headlineLarge),
        headlineMedium = apply(headlineMedium),
        headlineSmall = apply(headlineSmall),
        titleLarge = apply(titleLarge),
        titleMedium = apply(titleMedium),
        titleSmall = apply(titleSmall),
        bodyLarge = apply(bodyLarge),
        bodyMedium = apply(bodyMedium),
        bodySmall = apply(bodySmall),
        labelLarge = apply(labelLarge),
        labelMedium = apply(labelMedium),
        labelSmall = apply(labelSmall)
    )
}

@Composable
fun NebulaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    fontSelection: String = "default",
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Material You: динамическая палитра из обоев на Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val family = remember(fontSelection) { fontFamilyFor(fontSelection) }
    val typography = remember(fontSelection) { Typography().withFamily(family) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = NebulaShapes,
        content = content
    )
}