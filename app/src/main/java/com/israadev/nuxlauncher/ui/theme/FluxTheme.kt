package com.israadev.nuxlauncher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Flux theme — Google Material 3.
 *
 * Satu-satunya tempat MaterialTheme dikonfigurasi. Semua komponen M3 dari Google
 * (Switch, Slider, DropdownMenu, TextButton, dst.) otomatis ikut warna di bawah
 * ini, jadi screen tidak perlu menulis hex sendiri.
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
    surfaceContainerLowest = NuxColors.SurfaceInput,
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

/**
 * Type scale Material 3 standar Google: font sistem (Roboto), bobot reguler,
 * tanpa metrik kustom di label.
 */
private val FluxTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 47.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

private val FluxShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
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
