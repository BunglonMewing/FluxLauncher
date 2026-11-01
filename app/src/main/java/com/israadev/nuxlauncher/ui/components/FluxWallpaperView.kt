package com.israadev.nuxlauncher.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Wallpaper Minecraft otomatis dengan transisi fade.
 *
 * Menerima StateFlow path file lokal dari WallpaperEngine; gambar baru dicrossfade
 * masuk setiap kali engine memutasi Current wallpaper-nya (mi contoh tiap 1–5 menit).
 * Kalau belum ada gambar, kontainer kosong (bukan error) — pemanggil menentukan fallback.
 */
@Composable
fun FluxWallpaperView(
    wallpaperPath: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1f
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = wallpaperPath,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 1500)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 1500))
            },
            label = "wallpaper_fade",
            modifier = Modifier.fillMaxSize()
        ) { path ->
            if (path != null) {
                val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
                    value = withContext(Dispatchers.IO) {
                        try {
                            val f = java.io.File(path)
                            if (f.exists() && f.length() > 0) {
                                BitmapFactory.decodeFile(f.absolutePath)?.asImageBitmap()
                            } else null
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
                bitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Minecraft Wallpaper",
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize(),
                        alpha = alpha
                    )
                }
            }
        }
    }
}
