package com.israadev.nuxlauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * FLUX Google Material 3 palette ("Google Graphite + Blue").
 *
 * Tampilan UI kini mengikuti Material 3 standar dari Google:
 * primary biru Google, surface netral gelap bertingkat, outline klasik M3.
 *
 * Aturan pakai:
 * - Warna diambil dari sini, JANGAN hardcode hex baru di screen/dialog.
 * - Nama (ForestGreen, MintGreen, CardBorder, dst.) sengaja dipertahankan
 *   supaya ribuan call-site lama tetap kompilasi. Arti barunya mengikuti
 *   peran Material 3 dari Google:
 *     ForestGreen      -> primary          (Google Blue)
 *     MintGreen        -> onPrimaryContainer / aksen terang
 *     SoftLime         -> primaryContainer
 *     SurfaceWhite     -> surfaceContainerLow (kartu)
 *     SurfaceElevated  -> surfaceContainerHigh
 *     SurfaceInput     -> surfaceContainerLowest untuk input
 *     CardBorder       -> outlineVariant
 *     DarkGray         -> onSurface
 *     GrayNeutral      -> onSurfaceVariant
 */
object NuxColors {
    // Primary — Google Blue (Material 3 dark roles)
    val ForestGreen = Color(0xFFA8C7FA)   // primary 80
    val MintGreen = Color(0xFFC2D9FF)     // aksen terang (onPrimaryContainer-ish)
    val SageGreen = Color(0xFF7EACF0)     // inversePrimary
    val SoftLime = Color(0xFF0B57D0)      // primaryContainer (Google Blue 40)
    val LightGreen = Color(0x33A8C7FA)    // glow 20%
    val OnPrimary = Color(0xFF062E6F)     // teks di atas primary

    // Surface ladder (Material 3 dark surfaces — netral gelap khas Google)
    val Background = Color(0xFF0F1014)
    val SurfaceLow = Color(0xFF16181D)
    val SurfaceWhite = Color(0xF2161820)
    val SurfaceElevated = Color(0xFF1E2026)
    val SurfaceHigh = Color(0xFF23262E)
    val SurfaceInput = Color(0xFF121318)
    val Outline = Color(0xFF8E9199)       // outline klasik M3 dark
    val OutlineVariant = Color(0xFF35373E)

    // Teks
    val DarkGray = Color(0xFFE8EAF0)      // onSurface
    val GrayNeutral = Color(0xFFADB2BE)   // onSurfaceVariant
    val TextPrimary = Color(0xFFF5F7FA)

    // Garis & status
    val LightGray = Color(0x1FFFFFFF)
    val CardBorder = Color(0x3335443A)
    val ErrorRed = Color(0xFFFFB4AB)      // error 80 (M3)
    val Amber = Color(0xFFF7C36B)         // tertiary / warning

    // Alias lama (dipertahankan untuk kompatibilitas call-site)
    val Mint = SoftLime
    val MintDark = MintGreen
    val ForestDark = DarkGray
    val ForestLight = GrayNeutral
    val ForestMuted = GrayNeutral
    val Cream = Background
    val Coral = ErrorRed
    val TextMuted = GrayNeutral
    val SuccessGreen = ForestGreen
    val SkyBlue = Color(0xFFA8C7FA)
}

/**
 * Bentuk & jarak — mengikuti sudut baku Material 3.
 */
object NuxSizes {
    val BorderWidth = 1.dp
    val CornerRadius = 16.dp
    val CornerRadiusLarge = 24.dp
    val CornerRadiusSmall = 12.dp
    val CornerRadiusXs = 8.dp
    val ShadowOffset = 0.dp
    val BorderDefault = BorderStroke(BorderWidth, NuxColors.CardBorder)
    val ShapeDefault = RoundedCornerShape(CornerRadius)
    val ShapeLarge = RoundedCornerShape(CornerRadiusLarge)
    val ShapeSmall = RoundedCornerShape(CornerRadiusSmall)
    val ShapePill = RoundedCornerShape(percent = 50)

    /** Chip metadata memakai sudut baku kecil (Material 3). */
    val ShapeHudChip = RoundedCornerShape(6.dp)
}
