package com.israadev.nuxlauncher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Flux Material 3 theme.
 *
 * Satu-satunya tempat MaterialTheme dikonfigurasi. Semua komponen M3
 * (Switch, Slider, DropdownMenu, TextButton, dst.) otomatis ikut warna
 * di bawah ini, jadi screen tidak perlu menulis hex sendiri.
 */
private val FluxColorScheme = darkColorScheme(
    primary = NuxColors.ForestGreen,
    onPrimary = NuxColors.OnPrimary,
    primaryContainer = NuxColors.SoftLime,
    onPrimaryContainer = NuxColors.MintGreen,
    inversePrimary = NuxColors.SageGreen,
    secondary = NuxColors.MintGreen,
    onSecondary = NuxColors.OnPrimary,
    secondaryContainer = NuxColors.SurfaceHigh,
    onSecondaryContainer = NuxColors.DarkGray,
    tertiary = NuxColors.Amber,
    onTertiary = Color(0xFF3A2600),
    tertiaryContainer = Color(0xFF4A3410),
    onTertiaryContainer = Color(0xFFFFE0AE),
    error = NuxColors.ErrorRed,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = NuxColors.Background,
    onBackground = NuxColors.DarkGray,
    surface = NuxColors.SurfaceLow,
    onSurface = NuxColors.DarkGray,
    surfaceVariant = NuxColors.SurfaceHigh,
    onSurfaceVariant = NuxColors.GrayNeutral,
    surfaceContainerLowest = NuxColors.Background,
    surfaceContainerLow = NuxColors.SurfaceLow,
    surfaceContainer = NuxColors.SurfaceInput,
    surfaceContainerHigh = NuxColors.SurfaceElevated,
    surfaceContainerHighest = NuxColors.SurfaceHigh,
    outline = NuxColors.Outline,
    outlineVariant = NuxColors.OutlineVariant,
    scrim = Color(0xFF000000),
    inverseSurface = NuxColors.DarkGray,
    inverseOnSurface = NuxColors.SurfaceLow,
    surfaceTint = NuxColors.ForestGreen
)

private val FluxTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-1).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.6.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.8.sp
    )
)

private val FluxShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FluxNuxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FluxColorScheme,
        typography = FluxTypography,
        shapes = FluxShapes,
        content = content
    )
}
