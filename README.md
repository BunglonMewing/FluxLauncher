<div align="center">

# ⚡ FluxLauncher

### *Minecraft Java Edition launcher untuk Android — Material 3, dibuat untuk main, bukan untuk mainan.*

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Upstream](https://img.shields.io/badge/Basis%20Kode-ZalithLauncher2-purple.svg)](https://github.com/ZalithLauncher/ZalithLauncher2)
[![Android Build](https://github.com/BunglonMewing/FluxLauncher/actions/workflows/android-build.yml/badge.svg)](https://github.com/BunglonMewing/FluxLauncher/actions/workflows/android-build.yml)

</div>

> **⚠️ CATATAN PENTING**
> **FluxLauncher adalah proyek independen dan TIDAK RESMI.** Proyek ini dibangun dari basis kode
> sumber terbuka **[ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2)** dan
> **[PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)** di bawah lisensi
> **GNU General Public License v3.0 (GPL-3.0)**.
> FluxLauncher **tidak berafiliasi, tidak didukung, dan tidak disetujui** oleh tim ZalithLauncher,
> tim PojavLauncher, Mojang Studios, maupun Microsoft Corporation.

---

## 📋 Daftar Isi

- [Tentang FluxLauncher](#-tentang-fluxlauncher)
- [Desain Antarmuka (Material 3 + Gaming)](#-desain-antarmuka-material-3--gaming)
- [Fitur](#-fitur)
- [Arsitektur & Teknologi](#️-arsitektur--teknologi)
- [Struktur Proyek](#-struktur-proyek)
- [Panduan Build](#-panduan-build)
- [Catatan untuk Kontributor & AI](#-catatan-untuk-kontributor--ai)
- [Keamanan & Privasi](#-keamanan--privasi)
- [Kepatuhan Lisensi (GPL-3.0)](#-kepatuhan-lisensi-gpl-30)
- [Kredit Hulu](#-kredit-hulu-upstream-credits)
- [Penafian Hukum](#️-penafian-hukum)

---

## ⚡ Tentang FluxLauncher

FluxLauncher menjalankan **Minecraft Java Edition** di Android: unduh versi resmi dari manifest Mojang,
pasang modloader, kelola instance, lalu mainkan lewat runtime Java + renderer translasi OpenGL.
Semua fungsi inti gratis dan terbuka.

Repositori ini adalah kelanjutan internal dari basis kode ZalithLauncher2/PojavLauncher dengan
**lapisan antarmuka baru**: Material 3, satu bahasa visual, dan identitas visual bergaya gaming yang
tidak mengorbankan keterbacaan.

- **Aplikasi:** FluxLauncher
- **Repository:** [BunglonMewing/FluxLauncher](https://github.com/BunglonMewing/FluxLauncher)
- **Package / applicationId:** `com.israadev.nuxlauncher` (identifier internal dipertahankan agar
  kompatibel dengan data, skin server, dan konfigurasi yang sudah ada)
- **Versi:** 1.1.5 (versionCode 16)

---

## 🎨 Desain Antarmuka (Material 3 + Gaming)

Flux memakai sistem desain **"Reactor"**: Material 3 murni, dengan detail HUD secukupnya.

**Prinsip**
1. **Surface tonal, bukan gradient bertumpuk.** Kedalaman dibentuk lewat tangga surface
   (`#070A08` → `#0E1411` → `#19231C` → `#1F2A22`) dan outline tipis 1dp.
2. **Satu aksen utama.** Primary emerald `#10C98A`. Aksen lain hanya untuk status (amber) dan error.
3. **Detail gaming tanpa ornamen.** Chip metadata memakai sudut terpotong (*dog-tag*) dan label
   monospace (`FABRIC`, `JRE 21`, `1.21.4`) — bahasa visual HUD, bukan stiker.
4. **Navigasi jelas.** Navigation rail Material 3 dengan indikator pill, label selalu terlihat.

**Peta file desain**

| File | Isi |
|---|---|
| `ui/theme/NuxDesignTokens.kt` | Palet `NuxColors` + `NuxSizes` (satu-satunya sumber warna) |
| `ui/theme/FluxTheme.kt` | `FluxNuxTheme`: ColorScheme, Typography, Shapes Material 3 |
| `ui/components/NuxComponents.kt` | `NuxCard`, `NuxButton`, `NuxBadge`, `NuxTextField`, `NuxDialog`, `FluxPill`, `FluxIconButton` |
| `ui/components/NuxSidebar.kt` | Navigation rail |
| `ui/screens/DashboardScreen.kt` | Shell: rail, hero banner, top bar, launch dock |

Detail token, guardrail, dan riwayat perubahan ada di **[AI_NOTES.md](AI_NOTES.md)**.

---

## ✨ Fitur

### 🎮 Antarmuka
- 100% Jetpack Compose + Material 3, tema gelap "Reactor Graphite".
- Layout landscape adaptif: ponsel, foldable, tablet, dan emulator (skala DPI otomatis).
- Hero banner dinamis: gambar bawaan atau video latar pilihan sendiri (maks. 20 detik) dengan rotasi.
- Dialog seragam untuk seluruh alur (instance, renderer, akun, unduhan, crash, lisensi).

### ⚡ Runtime & Renderer
- **Multi-Java Runtime** (Java 8, 17, 21) dengan pemilihan otomatis sesuai versi Minecraft.
- **Renderer:** MobileGlues (DirectGLES), Holy GL4ES, VirGL, Zink, serta ANGLE — tinggal pilih profil.
- Alokasi RAM, initial heap, argumen JVM kustom, rasio resolusi render, dan batas performa.

### 📦 Instance, Mod & Konten
- **Multi-instance terisolasi** — versi & set mod terpisah tanpa tumpang tindih.
- **Modloader 1-klik**: Fabric, Forge, NeoForge, Quilt.
- **Integrasi Modrinth**: cari & pasang mod, modpack, resource pack, shader, datapack dari dalam app.
- Import file `.jar` / `.mrpack` / `.zip` langsung dari file manager (*open with*).

### 🕹️ Kontrol
- Editor kontrol visual: posisi, ukuran, transparansi, warna, dan keycode per tombol.
- Emulasi mouse sentuh + giroskop; dukungan gamepad Bluetooth/USB (Xbox, PlayStation, generic).
- Dukungan mouse fisik.

### 👥 Akun & Komunitas
- Login **Microsoft OAuth 2.0** resmi, **Ely.by**, dan akun offline.
- Wardrobe skin, profil launcher (username & avatar), presence "in-game".
- Voice room berbasis LiveKit dan daftar teman.

### 🧰 Utilitas
- AI assistant bawaan untuk bantuan & analisis crash.
- Perekam layar in-game (resolusi & FPS dapat diatur).
- Cek update APK otomatis + pengumuman dari server.

---

## 🛠️ Arsitektur & Teknologi

| Lapisan | Teknologi |
|---|---|
| Bahasa | Kotlin 2.4, Java (bridge SDL/LWJGL), C/C++ native (prebuilt `.so`) |
| UI | Jetpack Compose, Material 3, Compose Animation, Coroutines/Flow |
| Runtime | Android NDK bridge, LWJGL Mobile, OpenJDK 8/17/21 |
| Renderer | MobileGlues, GL4ES, VirGL, Zink, ANGLE, SPIRV-Cross |
| Jaringan | OkHttp, Gson, resolver DNS kustom |
| Autentikasi | Microsoft OAuth 2.0 (PKCE), Ely.by Authlib |
| Voice | LiveKit WebRTC via WebView bridge |
| Build | AGP 9.3.0, Gradle 9.5.0, Kotlin 2.4.10 |

---

## 📂 Struktur Proyek

```text
FluxLauncher/
├── app/
│   ├── build.gradle.kts
│   ├── libs/                          # AAR/JAR native (LWJGL, GL4ES, SDL, Zink, OpenAL)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/                    # Aset statis & jembatan LiveKit
│       ├── jniLibs/arm64-v8a/         # Library native (.so)
│       ├── res/                       # Tema window, ikon, drawable
│       └── java/
│           ├── com/israadev/nuxlauncher/
│           │   ├── MainActivity.kt        # Auth gate → Dashboard
│           │   ├── GameActivity.kt        # Surface game & input
│           │   ├── core/                  # Logika: account, auth, download, launch,
│           │   │                          #   instance, mods, renderer, runtime, social,
│           │   │                          #   crash, settings, update, skin, recorder
│           │   └── ui/
│           │       ├── theme/             # NuxDesignTokens, FluxTheme, NuxResponsive
│           │       ├── components/        # NuxCard, NuxButton, NuxSidebar, PlayerSkin, ...
│           │       ├── screens/           # Dashboard, Auth, Accounts, Mods, AI, Settings, Friends
│           │       ├── dialogs/           # 19 dialog (instance, renderer, akun, crash, ...)
│           │       ├── control/           # Layout mouse & kontrol in-game
│           │       └── activities/        # ErrorActivity
│           ├── com/movtery/...            # Bridge ZalithLauncher (SDL, input, native)
│           └── org/lwjgl, org/libsdl      # Bridge LWJGL/GLFW & SDL
├── AI_NOTES.md                        # Peta kode, design system, change log untuk AI
├── gradle/libs.versions.toml
├── local.properties.example
├── LICENSE                            # GPL-3.0
└── README.md
```

---

## 🚀 Panduan Build

### Prasyarat

| Kebutuhan | Versi |
|---|---|
| Android Studio | Ladybug (2024.2.1) atau lebih baru |
| JDK | 17 |
| Android SDK | compileSdk `37`, targetSdk `34`, minSdk `26` |
| Android NDK | r25+ (hanya bila menyentuh kode native) |
| Perangkat/emulator | arm64-v8a (satu-satunya ABI yang dibundel) |

### Langkah

1. **Clone**
   ```bash
   git clone https://github.com/BunglonMewing/FluxLauncher.git
   cd FluxLauncher
   ```

2. **Siapkan `local.properties`**
   ```bash
   cp local.properties.example local.properties
   ```
   Isi lokasi Android SDK:
   ```properties
   sdk.dir=/path/ke/Android/Sdk
   ```

   Opsional, kalau memakai server sendiri:
   ```properties
   # nux.server.url=https://domain-server-anda.com
   # nux.server.api.key=...
   ```

3. **Build debug**
   ```bash
   ./gradlew assembleDebug
   ```
   Windows:
   ```cmd
   gradlew.bat assembleDebug
   ```

4. **Hasil**
   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```

> Build release saat ini memakai signing config debug (`isMinifyEnabled = false`). Untuk distribusi
> publik, siapkan keystore sendiri dan jangan pernah commit file keystore.

### Continuous Integration

Workflow `.github/workflows/android-build.yml` berjalan otomatis:

| Trigger | Yang dilakukan |
|---|---|
| Push ke `main` / `feat/**`, PR ke `main`, manual | `assembleDebug` + `assembleRelease`, APK diunggah sebagai artifact (14 hari) |
| Push tag `v*` | APK yang sama dilampirkan ke GitHub Release |

Catatan: APK release dari CI masih ditandatangani debug keystore, jadi hanya untuk pengujian,
bukan untuk Play Store. Kalau butuh build bertanda tangan rilis, tambahkan keystore lewat
repository secrets dan sesuaikan `signingConfig` di `app/build.gradle.kts`.

---

## 🤖 Catatan untuk Kontributor & AI

- Semua warna, radius, dan tipografi berasal dari `ui/theme/`. Jangan menulis hex baru di screen.
- Gunakan komponen yang sudah ada (`NuxCard`, `NuxButton`, `NuxBadge`, `NuxTextField`, `NuxDialog`)
  sebelum membuat yang baru.
- Perubahan visual **tidak boleh** mengubah logika di `core/` (launch, download, auth, renderer).
- Setiap perubahan UI dicatat satu baris di `AI_NOTES.md` (bagian Change Log).
- Teks UI memakai `tr("key")` / `trStr("key", ...)` agar tetap multi-bahasa.

---

## 🔒 Keamanan & Privasi

- **Tanpa commit rahasia**: API key server dan AI dibaca dari `local.properties` (tidak masuk git).
- **Tidak menyimpan password**: autentikasi Microsoft berjalan di halaman resmi Microsoft (OAuth).
- **HTTPS/TLS** untuk seluruh komunikasi akun, katalog mod, dan unduhan aset.
- **Izin minimum**: storage (unduhan & instance), audio (voice room), media (rekam layar).

---

## 📜 Kepatuhan Lisensi (GPL-3.0)

1. **Sumber terbuka penuh** — seluruh kode klien FluxLauncher tersedia di repositori ini.
2. **Bebas dimodifikasi & didistribusikan ulang** sesuai ketentuan GNU GPL v3.
3. **Fungsi inti gratis** — unduh Minecraft, modloader, mod, dan runtime Java tidak dikunci.
4. Fitur tambahan opsional (mis. layanan cloud) tidak membatasi kebebasan fungsional inti GPL.

---

## 💖 Kredit Hulu (Upstream Credits)

- **[ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2)** — Movtery & kontributor.
  Basis arsitektur dan runtime Android modern (GPL-3.0).
- **[PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher)** — artdeell, khanhtran, dan tim.
  Pelopor eksekusi Minecraft Java di Android via LWJGL/GLFW (GPL-3.0).
- **[MobileGlues](https://github.com/MobileGlues/MobileGlues)** — penerjemah OpenGL ES → DirectGLES (LGPL-2.1).
- **[GL4ES](https://github.com/ptitSeb/gl4es)** — ptitSeb. Penerjemah OpenGL → OpenGL ES (MIT).
- **[Fold Craft Launcher](https://github.com/FCL-Team/FoldCraftLauncher)** — inspirasi integrasi modul (GPL-3.0).
- **[Ely.by](https://ely.by)** — infrastruktur skin & autentikasi komunitas.
- **[Modrinth](https://modrinth.com)** — API publik katalog mod, resource pack, dan shader.
- **[LiveKit](https://livekit.io)** — infrastruktur WebRTC untuk voice room.

---

## ⚠️ Penafian Hukum

- **BUKAN PRODUK RESMI MINECRAFT.**
- **NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**
- *Minecraft* adalah merek dagang **Mojang AB / Microsoft Corporation**.
- FluxLauncher adalah perangkat lunak pihak ketiga independen. Aset dan file game diunduh langsung
  dari server distribusi resmi Mojang sesuai lisensi akun pengguna.
- Proyek ini dirilis di bawah **GNU General Public License v3.0 (GPL-3.0)**.

---

<div align="center">

**FluxLauncher** — fork tidak resmi. Dibuat oleh komunitas, untuk komunitas.

</div>
