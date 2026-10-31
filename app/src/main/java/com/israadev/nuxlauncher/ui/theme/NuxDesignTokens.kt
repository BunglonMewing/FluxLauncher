package com.israadev.nuxlauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * FLUX "Reactor Graphite" palette.
 *
 * Aturan pakai:
 * - Warna diambil dari sini, JANGAN hardcode hex baru di screen/dialog.
 * - Nama lama (ForestGreen, MintGreen, CardBorder, dst.) sengaja dipertahankan
 *   supaya ribuan call-site lama tetap kompilasi. Arti barunya mengikuti
 *   peran Material 3:
 *     ForestGreen      -> primary          (brand emerald)
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
    // Primary — Reactor Emerald
    val ForestGreen = Color(0xFF10C98A)
    val MintGreen = Color(0xFF6CF2BE)
    val SageGreen = Color(0xFF0A8F63)
    val SoftLime = Color(0xFF0A2C20)
    val LightGreen = Color(0x2910C98A)
    val OnPrimary = Color(0xFF00291C)

    // Surface ladder (near-black dengan tint hijau tipis)
    val Background = Color(0xFF070A08)
    val SurfaceLow = Color(0xFF0E1411)
    val SurfaceWhite = Color(0xF20E1411)
    val SurfaceElevated = Color(0xFF19231C)
    val SurfaceHigh = Color(0xFF1F2A22)
    val SurfaceInput = Color(0xFF101813)
    val Outline = Color(0xFF3E4C44)
    val OutlineVariant = Color(0xFF253029)

    // Teks
    val DarkGray = Color(0xFFE9F3ED)
    val GrayNeutral = Color(0xFF9CAEA4)
    val TextPrimary = Color(0xFFF4FBF6)

    // Garis & status
    val LightGray = Color(0x1FFFFFFF)
    val CardBorder = Color(0x3335443A)
    val ErrorRed = Color(0xFFFF8C9A)
    val Amber = Color(0xFFF7C36B)

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
    val SkyBlue = Color(0xFF7FD8F7)
}

/**
 * Bentuk & jarak. Radius dibesarkan sedikit mengikuti Material 3 Expressive,
 * tapi chip metadata memakai sudut terpotong (CutCornerShape) sebagai penanda
 * "HUD" khas Flux.
 */
object NuxSizes {
    val BorderWidth = 1.dp
    val CornerRadius = 18.dp
    val CornerRadiusLarge = 24.dp
    val CornerRadiusSmall = 12.dp
    val CornerRadiusXs = 8.dp
    val ShadowOffset = 0.dp
    val BorderDefault = BorderStroke(BorderWidth, NuxColors.CardBorder)
    val ShapeDefault = RoundedCornerShape(CornerRadius)
    val ShapeLarge = RoundedCornerShape(CornerRadiusLarge)
    val ShapeSmall = RoundedCornerShape(CornerRadiusSmall)
    val ShapePill = RoundedCornerShape(percent = 50)

    /** Chip metadata bergaya dog-tag / HUD. */
    val ShapeHudChip = CutCornerShape(topStart = 2.dp, topEnd = 8.dp, bottomEnd = 2.dp, bottomStart = 8.dp)
}
