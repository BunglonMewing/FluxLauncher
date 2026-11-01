package com.israadev.nuxlauncher.core.wallpaper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.israadev.nuxlauncher.core.network.NuxDns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Engine wallpaper Minecraft otomatis.
 *
 * Mengambil gambar wallpaper bertema Minecraft dari internet (Wallhaven API publik,
 * tanpa API key), menyimpannya di cache lokal, lalu menggantinya secara berkala
 * (interval bisa diatur dari Pengaturan, contoh: tiap 1–5 menit).
 *
 * UI hanya perlu mengamati [currentWallpaper] dan menampilkan fade antar gambar.
 */
object WallpaperEngine {

    private const val TAG = "WallpaperEngine"
    private const val SEARCH_URL =
        "https://wallhaven.cc/api/v1/search?q=minecraft&categories=100&purity=100&ai_art_filter=1&sorting=random"
    private const val KEEP_FILES = 6          // Jumlah gambar yang dipertahankan di cache
    private const val MIN_WIDTH = 1600        // Minimal lebar supaya kualitas tetap bagus
    private const val MAX_TOTAL_FILES = 24

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .dns(NuxDns)
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private val _currentWallpaper = MutableStateFlow<File?>(null)
    /** File wallpaper yang sedang tampil (lokal, sudah terunduh). */
    val currentWallpaper: StateFlow<File?> = _currentWallpaper

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    var rotationJob: Job? = null
        private set

    private var cursor = 0

    fun getWallpapersDir(context: Context): File =
        File(context.cacheDir, "flux_wallpapers").apply { if (!exists()) mkdirs() }

    /**
     * Memulai siklus wallpaper. Dipanggil ulang saat interval/toggle berubah —
     * job lama otomatis dibatalkan. Setiap tick: refresh batch (bila perlu),
     * lalu pindah ke gambar berikutnya.
     */
    fun start(context: Context, enabled: Boolean, intervalMinutes: Int) {
        rotationJob?.cancel()
        if (!enabled) {
            _currentWallpaper.value = null
            return
        }
        val intervalMs = intervalMinutes.coerceIn(1, 30) * 60_000L
        rotationJob = scope.launch {
            while (true) {
                try {
                    val list = getWallpapersDir(context).listFiles { f -> f.extension == "jpg" }
                        ?.sortedByDescending { it.lastModified() }
                        ?.toList()
                        ?: emptyList()
                    if (list.size < 2) refreshWallpapers(context)
                    val ready = getWallpapersDir(context).listFiles { f -> f.extension == "jpg" }
                        ?.sortedByDescending { it.lastModified() }
                        ?.toList()
                        ?: emptyList()
                    if (ready.isNotEmpty()) {
                        if (cursor >= ready.size || _currentWallpaper.value?.absolutePath !in ready.map { it.absolutePath }) {
                            cursor = ready.indexOfFirst { it.absolutePath == _currentWallpaper.value?.absolutePath }
                            cursor = if (cursor < 0) 0 else (cursor + 1) % ready.size
                        } else {
                            cursor = (cursor + 1) % ready.size
                        }
                        _currentWallpaper.value = ready[cursor]
                    }
                    // Primerkan gambar berikutnya supaya transisi fade mulus tanpa jeda
                    preFetchNext(context, ready)
                } catch (e: Exception) {
                    Log.w(TAG, "Wallpaper cycle error: ${e.message}")
                }
                delay(intervalMs)
            }
        }
    }

    /**
     * Mengganti wallpaper besok-tik (tanpa menunggu interval) — dipakai tombol "Next" di UI.
     */
    fun next(context: Context) {
        val ready = getWallpapersDir(context).listFiles { f -> f.extension == "jpg" }
            ?.sortedByDescending { it.lastModified() }
            ?: return
        if (ready.isEmpty()) return
        cursor = (cursor + 1) % ready.size
        _currentWallpaper.value = ready[cursor]
    }

    private suspend fun preFetchNext(context: Context, ready: List<File>) {
        if (ready.size < 2) return
        val next = ready[(cursor + 1) % ready.size]
        // Warm-up decoder cache: unduh ulang tidak perlu, cukup pastikan file ada & valid
        if (!next.exists() || next.length() == 0L) {
            next.delete()
            refreshWallpapers(context)
        }
    }

    /**
     * Mengambil daftar wallpaper Minecraft dari internet lalu mengunduh
     * enam gambar terbaik ke cache. File lama dipangkas agar storage tetap hemat.
     */
    suspend fun refreshWallpapers(context: Context): Result<Int> = withContext(Dispatchers.IO) {
        val dir = getWallpapersDir(context)
        try {
            _isRefreshing.value = true
            val req = Request.Builder()
                .url("$SEARCH_URL&page=1")
                .header("User-Agent", "FluxLauncher/1.1 (Android launcher wallpaper)")
                .build()
            val resp = client.newCall(req).execute()
            resp.use { r ->
                if (!r.isSuccessful) return@withContext Result.failure(IOException("Wallhaven HTTP ${r.code}"))
                val body = r.body?.string() ?: return@withContext Result.failure(IOException("Empty response"))
                val parsed = gson.fromJson(body, WallhavenResponse::class.java)
                val candidates = parsed.data
                    .filter { it.width >= MIN_WIDTH }
                    .filter { it.path.startsWith("https://") }
                    .distinctBy { it.id }
                    .shuffled()
                    .take(KEEP_FILES)
                if (candidates.isEmpty()) return@withContext Result.failure(IOException("No wallpapers found"))

                var downloaded = 0
                for (item in candidates) {
                    if (downloaded >= KEEP_FILES - 1) break // sisakan slot untuk 1 file lama
                    try {
                        val fileName = "wp_${item.id}.jpg"
                        val target = File(dir, fileName)
                        if (target.exists() && target.length() > 0) {
                            downloaded++
                            continue
                        }
                        val dlReq = Request.Builder()
                            .url(item.path)
                            .header("User-Agent", "FluxLauncher/1.1 (Android launcher wallpaper)")
                            .build()
                        val dl = client.newCall(dlReq).execute()
                        dl.use { d ->
                            if (d.isSuccessful) {
                                d.body?.byteStream()?.use { input ->
                                    FileOutputStream(target).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                if (target.exists() && target.length() > 0) downloaded++
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Wallpaper download failed: ${e.message}")
                    }
                }
                trimCache(dir)
                return@withContext if (downloaded > 0) Result.success(downloaded) else Result.failure(IOException("Download failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isRefreshing.value = false
        }
    }

    private fun trimCache(dir: File) {
        val files = dir.listFiles { f -> f.extension == "jpg" }?.sortedByDescending { it.lastModified() } ?: return
        files.drop(MAX_TOTAL_FILES).forEach { runCatching { it.delete() } }
        // Buang cache memorinya (file .0--sized dkk.)
        files.filter { it.length() == 0L }.forEach { runCatching { it.delete() } }
    }
}

/** Model respons Wallhaven API (publik, tanpa API key). */
data class WallhavenResponse(
    val data: List<WallhavenItem> = emptyList()
)

data class WallhavenItem(
    val id: String = "",
    val path: String = "",
    val width: Int = 0,
    val height: Int = 0
)
