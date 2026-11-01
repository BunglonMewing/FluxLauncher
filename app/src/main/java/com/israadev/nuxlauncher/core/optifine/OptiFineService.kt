package com.israadev.nuxlauncher.core.optifine

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.israadev.nuxlauncher.core.instance.InstanceManager
import com.israadev.nuxlauncher.core.models.Instance
import com.israadev.nuxlauncher.core.models.OptiFineVersionItem
import com.israadev.nuxlauncher.core.network.NuxDns
import com.oracle.dalvik.VMLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Dukungan OptiFine otomatis (headless, tanpa GUI installer) — mengikuti resep
 * yang dipakai HMCL & xmcl:
 *
 * 1. Daftar versi   : https://bmclapi2.bangbang93.com/optifine/<mcver>  (JSON)
 * 2. Installer jar  : https://bmclapi2.bangbang93.com/optifine/<mcver>/<type>/<patch>
 *                     (fallback: optifine.net adloadx → downloadx token)
 * 3. Patch jar      : `optifine.Patcher <client.jar> <installer.jar> <output.jar>`
 *                     dijalankan di dalam runtime Java bawaan launcher
 *                     (terbukti jalan pada eksekusi in-process yang sama dengan game).
 * 4. Launchwrapper  : diekstrak dari installer (launchwrapper-of-<v>.jar /
 *                     launchwrapper-2.0.jar); fallback klasik: net.minecraft:launchwrapper:1.12.
 * 5. Version JSON   : inheritsFrom, mainClass = net.minecraft.launchwrapper.Launch,
 *                     game args += --tweakClass optifine.OptiFineTweaker.
 */
object OptiFineService {
    private const val TAG = "OptiFineService"
    private const val BMCL_ROOT = "https://bmclapi2.bangbang93.com"
    private const val OPTIFINE_NET = "https://optifine.net"

    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .dns(NuxDns)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // ---------------------------------------------------------------------
    // Daftar versi
    // ---------------------------------------------------------------------

    /** BMCLAPI menggunakan "1.8.0" / "1.9.0" alih-alih "1.8" / "1.9". */
    private fun toLookupVersion(mcVersion: String): String = when (mcVersion) {
        "1.8" -> "1.8.0"
        "1.9" -> "1.9.0"
        else -> mcVersion
    }

    /**
     * Mengambil daftar build OptiFine untuk satu versi Minecraft.
     * Primary: BMCLAPI. Fallback: scraping halaman downloads optifine.net.
     */
    suspend fun getOptiFineVersions(mcVersion: String): Result<List<OptiFineVersionItem>> =
        withContext(Dispatchers.IO) {
            val look = toLookupVersion(mcVersion)
            val urls = listOf(
                "$BMCL_ROOT/optifine/$look",
                "$BMCL_ROOT/optifine/$mcVersion"
            )
            var lastException: Exception? = null

            for (url in urls) {
                try {
                    val req = Request.Builder().url(url)
                        .header("User-Agent", "FluxLauncher/1.1 (Android OptiFine)")
                        .build()
                    client.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val body = resp.body?.string()
                            if (!body.isNullOrBlank()) {
                                val items: Array<OptiFineVersionItem> =
                                    gson.fromJson(body, Array<OptiFineVersionItem>::class.java)
                                if (items.isNotEmpty()) {
                                    return@withContext Result.success(items.toList())
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    lastException = e
                }
            }

            // Fallback: scrape optifine.net/downloads
            try {
                val scraped = scrapeOptiFineNet(look)
                if (scraped.isNotEmpty()) return@withContext Result.success(scraped)
            } catch (e: Exception) {
                lastException = e
            }

            Result.failure(lastException ?: IOException("Cannot load OptiFine versions for $mcVersion"))
        }

    /**
     * Scraping halaman https://optifine.net/downloads — mengumpulkan fileName=
     * OptiFine_x.y.z_TYPE_RELEASE.jar lalu menguraikan versinya.
     */
    private fun scrapeOptiFineNet(mcVersion: String): List<OptiFineVersionItem> {
        val req = Request.Builder().url("$OPTIFINE_NET/downloads")
            .header("User-Agent", "Mozilla/5.0 (Linux; Android) FluxLauncher/1.1")
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return emptyList()
            val html = resp.body?.string() ?: return emptyList()
            val regex = Regex("OptiFine_(1\\.[0-9.]+)_(HD[USX]?(?:_Ultra)?(?:_C[0-9A-Z]*|_[A-Z][0-9A-Z]+))\\.jar")
            return regex.findAll(html)
                .distinctBy { it.value }
                .filter { it.groupValues[1] == mcVersion }
                .map { m ->
                    val mc = m.groupValues[1]
                    val rest = m.groupValues[2]
                    // rest misal: "HD_U_I6" (type=_U, patch=I6) atau "HD_B8" (type=HD_B, patch="8")
                    val pieces = rest.split("_").filter { it.isNotBlank() }
                    val type = if (pieces.isNotEmpty()) pieces[0] else "HD"
                    val patch = if (pieces.size > 1) pieces[1] else ""
                    OptiFineVersionItem(
                        mcversion = mc,
                        type = type,
                        patch = patch,
                        filename = m.value
                    )
                }
                .toList()
        }
    }

    // ---------------------------------------------------------------------
    // Unduh installer
    // ---------------------------------------------------------------------

    fun getInstallerCandidateUrls(item: OptiFineVersionItem): List<String> {
        val urls = mutableListOf<String>()
        urls.add("$BMCL_ROOT/optifine/${toLookupVersion(item.mcversion)}/${item.type}/${item.patch}")
        urls.add("$BMCL_ROOT/optifine/${item.mcversion}/${item.type}/${item.patch}")
        urls.add("$OPTIFINE_NET/adloadx?f=${item.filename}")
        return urls
    }

    private fun downloadInstaller(item: OptiFineVersionItem, targetFile: File) {
        targetFile.parentFile?.mkdirs()
        for (url in getInstallerCandidateUrls(item)) {
            try {
                val req = Request.Builder().url(url)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android) FluxLauncher/1.1")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    if (url.contains("adloadx")) {
                        // Halaman adload mengandung link downloadx?f=...&x=<token> → unduh jar asli
                        val html = resp.body?.string() ?: return@use
                        val link = Regex("downloadx\\?f=[A-Za-z0-9_.\\-]+&x=[A-Za-z0-9]+").find(html)?.value ?: return@use
                        val dlReq = Request.Builder().url("$OPTIFINE_NET/$link")
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android) FluxLauncher/1.1")
                            .build()
                        client.newCall(dlReq).execute().use { dl ->
                            if (!dl.isSuccessful) return@use
                            val bytes = dl.body?.bytes() ?: return@use
                            if (bytes.isEmpty() || bytes[0].toInt() != 0x50 || bytes[1].toInt() != 0x4B) return@use // bukan ZIP/JAR
                            FileOutputStream(targetFile).use { it.write(bytes) }
                            if (targetFile.length() > 0) return
                        }
                    } else {
                        val bytes = resp.body?.bytes() ?: return@use
                        if (bytes.isEmpty() || bytes[0].toInt() != 0x50 || bytes[1].toInt() != 0x4B) return@use
                        FileOutputStream(targetFile).use { it.write(bytes) }
                        if (targetFile.length() > 0) return
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Installer download gagal dari $url: ${e.message}")
            }
        }
        throw IOException("Failed to download OptiFine installer ${item.filename}")
    }

    // ---------------------------------------------------------------------
    // Instalasi (patch + launchwrapper + version json)
    // ---------------------------------------------------------------------

    /**
     * Pasang OptiFine untuk instance yang loader-nya "optifine".
     * Vanilla client jar harus sudah ada (sudah terunduh di tahap vanilla).
     * Mengembalikan versionId yang terpasang (contoh: "1.20.1-OptiFine_HD_U_I6").
     */
    fun installOptiFine(
        context: Context,
        instance: Instance,
        vanillaJar: File,
        onProgress: (Float, String) -> Unit
    ): Result<String> {
        try {
            val versionDir = File(InstanceManager.getVersionsDir(context), instance.mcVersion)
            val item = pickedVersionCache[instance.id]
                ?: throw IllegalStateException("No OptiFine build chosen for ${instance.mcVersion}")

            // 1. Installer jar
            val installerFile = File(versionDir, item.filename)
            if (!installerFile.exists() || installerFile.length() == 0L) {
                onProgress(0.92f, "Downloading OptiFine ${item.edition} installer...")
                downloadInstaller(item, installerFile)
            }

            // 2. Verifikasi metadata dari Config.class (MC_VERSION / OF_EDITION / OF_RELEASE)
            val meta = readInstallerMeta(installerFile)
            if (meta != null && meta.first.isNotBlank()) {
                val mcMatches = meta.first == instance.mcVersion ||
                        toLookupVersion(instance.mcVersion) == meta.first
                if (!mcMatches) {
                    // Installer tidak cocok dengan versi game instance — hentikan agar tidak merusak jar.
                    throw IllegalStateException(
                        "OptiFine installer is for MC ${meta.first}, but instance uses ${instance.mcVersion}"
                    )
                }
            }

            // 3. Patch vanilla jar → jar OptiFine
            val libDir = InstanceManager.getLibrariesDir(context)
            val ofLibFile = File(
                libDir,
                "optifine/OptiFine/${item.libraryVersion}/OptiFine-${item.libraryVersion}.jar"
            )
            ofLibFile.parentFile?.mkdirs()
            val hasPatcher = runCatching {
                java.util.zip.ZipFile(installerFile).use { zip ->
                    zip.getEntry("optifine/Patcher.class") != null
                }
            }.getOrDefault(false)

            if (hasPatcher) {
                onProgress(0.94f, "Patching OptiFine ${item.edition}...")
                val runtimeHome = pickRuntimeHome(context, instance.mcVersion)
                val exitCode = runJavaClass(
                    runtimeHome = runtimeHome,
                    classpath = installerFile.absolutePath,
                    mainClass = "optifine.Patcher",
                    args = listOf(vanillaJar.absolutePath, installerFile.absolutePath, ofLibFile.absolutePath),
                    tmpDir = context.cacheDir,
                    logTag = "OptiFine.Patcher"
                )
                if (exitCode != 0 || !ofLibFile.exists() || ofLibFile.length() == 0L) {
                    throw IllegalStateException("OptiFine Patcher failed (exit code $exitCode)")
                }
            } else {
                // Installer lama (<= 1.12 era): installer itu sendiri adalah jar modded.
                installerFile.copyTo(ofLibFile, overwrite = true)
            }

            // 4. Launchwrapper
            val launchwrapperCoords = extractLaunchwrapper(installerFile, libDir)
            if (launchwrapperCoords == null) {
                downloadClassicLaunchwrapper(libDir)
            }
            val lwCoord = launchwrapperCoords ?: "net.minecraft:launchwrapper:1.12"

            // 5. Tulis version json OptiFine
            // versionId mengikuti versi instance (bukan normalisasi BMCLAPI,
            // mis. "1.8" ≠ "1.8.0") supaya GameLauncher menemukan foldernya.
            val versionId = "${instance.mcVersion}-OptiFine_${item.edition}"
            val ofVersionDir = File(InstanceManager.getVersionsDir(context), versionId)
            if (!ofVersionDir.exists()) ofVersionDir.mkdirs()
            val versionJson = buildVersionJson(instance.mcVersion, versionId, item, lwCoord)
            File(ofVersionDir, "$versionId.json").writeText(versionJson)

            instance.loaderVersion = item.edition
            return Result.success(versionId)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memasang OptiFine: ${e.message}", e)
            return Result.failure(e)
        }
    }

    /** Cache build yang dipilih user saat membuat instance (dialog Add Instance). */
    val pickedVersionCache = mutableMapOf<String, OptiFineVersionItem>()

    /**
     * Fallback pemilihan build otomatis (dipakai bila instance dibuat tanpa cache),
     * memilih build terbaru yang stabil.
     */
    suspend fun pickBestVersion(context: Context, instance: Instance): OptiFineVersionItem? {
        pickedVersionCache[instance.id]?.let { return it }
        val list = getOptiFineVersions(instance.mcVersion).getOrNull() ?: return null
        val stable = list.lastOrNull { !it.isPreview } ?: list.lastOrNull()
        pickedVersionCache[instance.id] = stable ?: return null
        return stable
    }

    // ---------------------------------------------------------------------
    // Util: metadata installer (constant pool Config.class)
    // ---------------------------------------------------------------------

    /**
     * Membaca constant-pool Utf8 Config.class untuk menemukan MC_VERSION, OF_EDITION,
     * OF_RELEASE. Return Triple(mcVersion, edition, release) atau null bila tidak ada.
     */
    private fun readInstallerMeta(installer: File): Triple<String, String, String>? {
        val candidates = listOf("net/optifine/Config.class", "Config.class", "notch/net/optifine/Config.class")
        return try {
            java.util.zip.ZipFile(installer).use { zip ->
                for (name in candidates) {
                    val entry = zip.getEntry(name) ?: continue
                    val cls = zip.getInputStream(entry).use { it.readBytes() }
                    val strings = extractUtf8ConstantsInOrder(cls)
                    fun after(marker: String): String? {
                        val idx = strings.indexOf(marker)
                        return if (idx >= 0 && idx + 1 < strings.size) strings[idx + 1] else null
                    }
                    val mcVersion = after("MC_VERSION")
                    val edition = after("OF_EDITION")
                    val release = after("OF_RELEASE")
                    if (mcVersion != null) return Triple(mcVersion, edition ?: "", release ?: "")
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Parser constant pool class-file sederhana: mengumpulkan seluruh konstanta Utf8 sesuai urutan. */
    private fun extractUtf8ConstantsInOrder(cls: ByteArray): List<String> {
        if (cls.size < 12) return emptyList()
        val pos = intArrayOf(8) // melompati magic + versi
        val cpCount = u2(cls, pos[0]).also { pos[0] += 2 }
        val result = mutableListOf<String>()
        var i = 1
        while (i < cpCount) {
            val tag = cls[pos[0]].toInt() and 0xFF
            when (tag) {
                1 -> { // Utf8
                    val len = u2(cls, pos[0] + 1)
                    result.add(String(cls, pos[0] + 3, len, Charsets.UTF_8))
                    pos[0] += 3 + len
                }
                7, 8, 16, 19, 20 -> pos[0] += 3
                9, 10, 11, 12, 17, 18 -> pos[0] += 5
                15 -> pos[0] += 4
                5, 6 -> { pos[0] += 9; i++ } // Long/Double memakai 2 slot
                else -> break
            }
            i++
        }
        return result
    }

    private fun u2(b: ByteArray, off: Int): Int = ((b[off].toInt() and 0xFF) shl 8) or (b[off + 1].toInt() and 0xFF)

    // ---------------------------------------------------------------------
    // Util: launchwrapper & jar klasik
    // ---------------------------------------------------------------------

    /** Ekstrak launchwrapper dari installer. Return koordinat maven bila sukses. */
    private fun extractLaunchwrapper(installer: File, libDir: File): String? {
        return try {
            java.util.zip.ZipFile(installer).use { zip ->
                val versionText = zip.getEntry("launchwrapper-of.txt")?.let {
                    zip.getInputStream(it).use { s -> s.readBytes().decodeToString().trim() }
                }
                if (!versionText.isNullOrBlank()) {
                    val jarName = "launchwrapper-of-$versionText.jar"
                    val entry = zip.getEntry(jarName) ?: return null
                    val target = File(libDir, "net/minecraft/launchwrapper/of-$versionText/$jarName")
                    target.parentFile?.mkdirs()
                    zip.getInputStream(entry).use { s ->
                        FileOutputStream(target).use { o -> s.copyTo(o) }
                    }
                    return "net.minecraft:launchwrapper:of-$versionText"
                }
                val entry20 = zip.getEntry("launchwrapper-2.0.jar")
                if (entry20 != null) {
                    val target = File(libDir, "net/minecraft/launchwrapper/2.0/launchwrapper-2.0.jar")
                    target.parentFile?.mkdirs()
                    zip.getInputStream(entry20).use { s ->
                        FileOutputStream(target).use { o -> s.copyTo(o) }
                    }
                    return "net.minecraft:launchwrapper:2.0"
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Unduh launchwrapper 1.12 klasik (untuk MC <= 1.12). */
    private fun downloadClassicLaunchwrapper(libDir: File) {
        val rel = "net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar"
        val target = File(libDir, rel)
        if (target.exists() && target.length() > 0) return
        val urls = listOf(
            "https://libraries.minecraft.net/$rel",
            "$BMCL_ROOT/maven/$rel",
            "https://repo1.maven.org/maven2/$rel"
        )
        for (url in urls) {
            try {
                val req = Request.Builder().url(url)
                    .header("User-Agent", "FluxLauncher/1.1 (Android OptiFine)")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val bytes = resp.body?.bytes() ?: return@use
                    if (bytes.isEmpty() || bytes[0].toInt() != 0x50 || bytes[1].toInt() != 0x4B) return@use
                    target.parentFile?.mkdirs()
                    FileOutputStream(target).use { it.write(bytes) }
                    if (target.length() > 0) return
                }
            } catch (e: Exception) {
                Log.w(TAG, "Ekstraktor launchwrapper gagal dari $url: ${e.message}")
            }
        }
        Log.w(TAG, "Gagal mengunduh launchwrapper 1.12")
    }

    // ---------------------------------------------------------------------
    // Util: menjalankan class Java di dalam runtime bawaan launcher
    // ---------------------------------------------------------------------

    /** Pilih runtime home untuk menjalankan Patcher: runtime yang direkomendasikan bila sudah ada, kalau tidak runtime mana pun yang terpasang. */
    private fun pickRuntimeHome(context: Context, mcVersion: String): File {
        val recommended = com.israadev.nuxlauncher.core.runtime.JavaRuntimeManager
            .getRecommendedRuntime(mcVersion, null)
        val recHome = com.israadev.nuxlauncher.core.runtime.JavaRuntimeManager
            .getRuntimeHome(context, recommended)
        val javaBin = File(recHome, "bin/java")
        if (javaBin.exists() && javaBin.length() > 0) return recHome

        // Fallback: runtime mana pun yang tersedia
        val order = listOf("jre-8", "jre-17", "jre-21", "jre-25")
        for (name in order) {
            val home = com.israadev.nuxlauncher.core.runtime.JavaRuntimeManager
                .getRuntimeHome(context, name)
            if (File(home, "bin/java").exists() && File(home, "bin/java").length() > 0) return home
        }
        throw IllegalStateException("No Java runtime installed yet. Download a version first, then retry OptiFine installation.")
    }

    /**
     * Menjalankan main class di runtime Java bawaan launcher:
     * 1) coba exec `bin/java` (clean process); 2) fallback: eksekusi in-process
     * lewat VMLauncher.launchJVM — mekanisme yang sama dengan game.
     * Return exit code.
     */
    private fun runJavaClass(
        runtimeHome: File,
        classpath: String,
        mainClass: String,
        args: List<String>,
        tmpDir: File,
        logTag: String
    ): Int {
        val javaBin = File(runtimeHome, "bin/java")
        com.israadev.nuxlauncher.core.runtime.JavaRuntimeManager.ensureExecutablePermissions(runtimeHome)
        val fullArgs = buildList {
            add(javaBin.absolutePath)
            add("-Djava.home=${runtimeHome.absolutePath}")
            add("-Djava.io.tmpdir=${tmpDir.absolutePath}")
            add("-Dos.name=Linux")
            add("-Xms32M")
            add("-Xmx384M")
            add("-cp")
            add(classpath)
            add(mainClass)
            addAll(args)
        }

        // 1) exec proses biasa
        try {
            val pb = ProcessBuilder(fullArgs)
            pb.redirectErrorStream(true)
            pb.environment()["LD_LIBRARY_PATH"] = listOf(
                File(runtimeHome, "lib/jli").absolutePath,
                File(runtimeHome, "lib/server").absolutePath,
                File(runtimeHome, "lib/client").absolutePath,
                File(runtimeHome, "lib").absolutePath
            ).joinToString(":") + ":" + System.getenv("LD_LIBRARY_PATH")
            pb.environment()["TMPDIR"] = tmpDir.absolutePath
            val proc = pb.start()
            val out = proc.inputStream.readBytes().decodeToString()
            val exitCode = proc.waitFor()
            Log.i(TAG, "[$logTag] exec result=$exitCode${out.takeLast(400)}")
            if (exitCode == 0 && File(args.last()).exists()) return 0
        } catch (e: Throwable) {
            Log.w(TAG, "[$logTag] exec gagal (${e.message}); mencoba VMLauncher in-process...")
        }

        // 2) VMLauncher (in-process, via JNI) — sama dengan peluncuran game
        try {
            val ldPaths = listOf(
                File(runtimeHome, "lib/jli").absolutePath,
                File(runtimeHome, "lib/server").absolutePath,
                File(runtimeHome, "lib/client").absolutePath,
                File(runtimeHome, "lib").absolutePath
            ).joinToString(":")
            android.system.Os.setenv("LD_LIBRARY_PATH", ldPaths, false)
            android.system.Os.setenv("TMPDIR", tmpDir.absolutePath, false)
            val cfg = listOf(
                javaBin.absolutePath,
                "-Djava.home=${runtimeHome.absolutePath}",
                "-Djava.io.tmpdir=${tmpDir.absolutePath}",
                "-Dos.name=Linux",
                "-Xms32M",
                "-Xmx512M",
                "-Dsun.stdout.encoding=UTF-8",
                "-Dsun.stderr.encoding=UTF-8",
                "-cp", classpath,
                mainClass
            ) + args
            val exitCode = VMLauncher.launchJVM(cfg.toTypedArray())
            Log.i(TAG, "[$logTag] VMLauncher result=$exitCode")
            return exitCode
        } catch (e: Throwable) {
            Log.e(TAG, "[$logTag] VMLauncher juga gagal: ${e.message}", e)
            return -1
        }
    }

    /** Bangun isi version json OptiFine (HMCL-style, standalone OptiFine path). */
    private fun buildVersionJson(mcVersion: String, versionId: String, item: OptiFineVersionItem, lwCoord: String): String {
        val libraries = com.google.gson.JsonArray()
        val lw = com.google.gson.JsonObject().also { it.addProperty("name", lwCoord) }
        val of = com.google.gson.JsonObject().also { it.addProperty("name", "optifine:OptiFine:${item.libraryVersion}") }
        libraries.add(of)
        libraries.add(lw)

        val gameArgs = com.google.gson.JsonArray().also {
            it.add("--tweakClass")
            it.add("optifine.OptiFineTweaker")
        }
        val arguments = com.google.gson.JsonObject().also { a ->
            a.add("game", gameArgs)
            a.add("jvm", com.google.gson.JsonArray())
        }

        return JsonParser.parseString(
            gson.toJson(
                mapOf(
                    "id" to versionId,
                    "inheritsFrom" to mcVersion,
                    "type" to "release",
                    "mainClass" to "net.minecraft.launchwrapper.Launch",
                    "arguments" to arguments,
                    "libraries" to libraries,
                    "minimumLauncherVersion" to 21
                )
            )
        ).toString()
    }
}
