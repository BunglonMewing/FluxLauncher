package com.israadev.nuxlauncher.core.models

import com.google.gson.annotations.SerializedName

/**
 * Mojang Version Manifest V2 models
 */
data class VersionManifest(
    val latest: LatestVersion,
    val versions: List<VersionItem>
)

data class LatestVersion(
    val release: String,
    val snapshot: String
)

data class VersionItem(
    val id: String,
    val type: String,
    val url: String,
    val time: String,
    val releaseTime: String
)

/**
 * Detailed Version JSON
 */
data class VersionDetail(
    val id: String,
    val mainClass: String?,
    val minecraftArguments: String?,
    val arguments: VersionArguments?,
    val downloads: VersionDownloads?,
    val libraries: List<Library>?,
    val assetIndex: AssetIndexInfo?,
    val assets: String?,
    val javaVersion: JavaVersionInfo? = null,
    val type: String?
)

data class JavaVersionInfo(
    val component: String?,
    val majorVersion: Int?
)

data class VersionArguments(
    val game: List<Any>?,
    val jvm: List<Any>?
)

data class VersionDownloads(
    val client: DownloadArtifact?,
    val server: DownloadArtifact?
)

data class DownloadArtifact(
    val path: String?,
    val sha1: String?,
    val size: Long?,
    val url: String?
)

data class Library(
    val name: String,
    val downloads: LibraryDownloads?,
    val rules: List<Rule>?,
    val natives: Map<String, String>?
)

data class LibraryDownloads(
    val artifact: DownloadArtifact?,
    val classifiers: Map<String, DownloadArtifact>?
)

data class Rule(
    val action: String, // "allow" or "disallow"
    val os: OsRule?
)

data class OsRule(
    val name: String? // "osx", "linux", "windows"
)

data class AssetIndexInfo(
    val id: String,
    val sha1: String,
    val size: Long,
    val totalSize: Long,
    val url: String
)

data class AssetIndexContent(
    val objects: Map<String, AssetObject>
)

data class AssetObject(
    val hash: String,
    val size: Long
)

/**
 * NUX Instance representation
 */
data class Instance(
    val id: String,
    val name: String,
    val mcVersion: String,
    val loader: String = "vanilla", // "vanilla", "fabric", or "optifine"
    var loaderVersion: String = "",
    val icon: String = "crafting_table",
    val javaRuntime: String = "auto", // "auto", "jre-8", "jre-17", "jre-21", "jre-25"
    val createdTime: Long = System.currentTimeMillis(),
    var isDownloaded: Boolean = false
)

/**
 * User Account representation
 */
data class UserAccount(
    val id: String = "",
    val username: String = "",
    val uuid: String = "",
    val accessToken: String? = "0",
    val refreshToken: String? = "0",
    val isOffline: Boolean = true,
    val isGuest: Boolean = false,
    val email: String? = "",
    val isActivated: Boolean = false,
    val tier: String? = "unactivated",
    val photoUrl: String? = "",
    val accountType: String? = "offline", // "offline", "microsoft", "elyby"
    val xuid: String? = null,
    val authServerUrl: String? = null,
    val skinUrl: String? = null,
    val skinModel: String? = "classic", // "classic" (steve) or "slim" (alex)
    val customSkinPath: String? = null,
    val customCapePath: String? = null
) {
    val safeAccountType: String
        get() = (accountType ?: if (isOffline) "offline" else "offline").lowercase()

    val safeSkinModel: String
        get() = skinModel?.ifBlank { "classic" } ?: "classic"

    val safeAccessToken: String
        get() = accessToken?.ifBlank { "0" } ?: "0"

    val safeRefreshToken: String
        get() = refreshToken?.ifBlank { "0" } ?: "0"

    val safeEmail: String
        get() = email ?: ""

    val safeTier: String
        get() = tier?.ifBlank { "unactivated" } ?: "unactivated"

    val hasCustomSkin: Boolean
        get() = !customSkinPath.isNullOrBlank() && java.io.File(customSkinPath).exists()

    val hasCustomCape: Boolean
        get() = !customCapePath.isNullOrBlank() && java.io.File(customCapePath).exists()

    fun getSkinFile(): java.io.File? = customSkinPath?.let { java.io.File(it) }?.takeIf { it.exists() }
    fun getCapeFile(): java.io.File? = customCapePath?.let { java.io.File(it) }?.takeIf { it.exists() }
}

/**
 * OptiFine version dari BMCLAPI (endpoint per versi MC).
 * Contoh payload: {"mcversion":"1.12.2","type":"HD_U","patch":"G5",
 *  "filename":"OptiFine_1.12.2_HD_U_G5.jar","forge":"Forge #2847"}
 * Preview: {"type":"HD_U_G6","patch":"pre1","filename":"preview_OptiFine_...jar"}
 */
data class OptiFineVersionItem(
    val mcversion: String = "",
    val type: String = "HD",
    val patch: String = "",
    val filename: String = "",
    val forge: String? = null,
    val date: String? = null
) {
    /** Edition = type + patch, contoh: "HD_U_G5" atau "HD_U_G6_pre1". */
    val edition: String
        get() = if (patch.isNotBlank()) "${type}_$patch" else type

    val isPreview: Boolean
        get() = patch.startsWith("pre") || patch.startsWith("alpha")

    /** Id version json: "1.20.1-OptiFine_HD_U_I6". */
    val versionId: String
        get() = "$mcversion-OptiFine_$edition"

    /** Koordinat maven: "1.20.1_HD_U_I6". */
    val libraryVersion: String
        get() = "${mcversion}_$edition"
}

/**
 * Fabric Meta Loader version
 */
data class FabricLoaderItem(
    val loader: FabricLoaderInfo
)

data class FabricLoaderInfo(
    val version: String,
    val stable: Boolean
)

/**
 * Fabric Profile JSON representation from meta.fabricmc.net
 */
data class FabricProfile(
    val id: String,
    val inheritsFrom: String,
    val mainClass: String = "net.fabricmc.loader.impl.launch.knot.KnotClient",
    val libraries: List<FabricLibrary> = emptyList()
)

data class FabricLibrary(
    val name: String,
    val url: String? = "https://maven.fabricmc.net/",
    val md5: String? = null,
    val sha1: String? = null,
    val sha256: String? = null,
    val sha512: String? = null,
    val size: Long? = null
)
