# AI_NOTES — FluxLauncher

Catatan kerja untuk agent/AI. Tujuannya: agent berikutnya tidak perlu membaca ulang 30.000 baris kode
untuk tahu "di mana mengubah apa" dan "apa yang sudah diubah".

> Aturan #1: file ini **selalu diperbarui** setiap ada perubahan kode.

---

## 0. Cara pakai file ini

1. Baca bagian **1 (Snapshot)** dan **2 (Peta Kode)** → langsung tahu lokasi kerja.
2. Mau ubah tampilan? Baca **3 (Design System)** dulu. Jangan bikin token/komponen baru kalau sudah ada.
3. Sebelum menulis kode, baca **4 (Guardrail)**.
4. Setelah selesai, tambahkan **satu baris** di **5 (Change Log)** — formatnya sudah baku, tinggal isi.
5. Jangan menulis esai di change log. Satu baris per perubahan, bahasa Indonesia singkat.

---

## 1. Snapshot Proyek

| Item | Nilai |
|---|---|
| Nama produk | FluxLauncher (repo: `BunglonMewing/FluxLauncher`) |
| Basis | Fork tidak resmi ZalithLauncher2 / PojavLauncher (GPL-3.0) |
| Bahasa | Kotlin + sedikit Java (Bridge SDL/LWJGL) |
| UI | 100% Jetpack Compose, Material 3 |
| Package / applicationId | `com.israadev.nuxlauncher` (**sengaja belum di-rename**) |
| Root project Gradle | `Flux-Launcher-Android` |
| versionName / versionCode | 1.1.5 / 16 |
| compileSdk / targetSdk / minSdk | 37 / 34 / 26 |
| AGP / Gradle / Kotlin | 9.3.0 / 9.5.0 / 2.4.10 |
| Orientasi | Landscape, fullscreen immersive |
| Branch kerja redesign | `feat/google-m3-optifine-wallpaper` |
| Entry point | `MainActivity` (auth gate → `DashboardScreen`), `GameActivity` (runtime game) |

---

## 2. Peta Kode

### UI (semua yang boleh diubah untuk tugas desain)

| Path | Isi | Catatan |
|---|---|---|
| `.github/workflows/android-build.yml` | CI build APK + release otomatis | JDK 21, platform 37, artifact `fluxlauncher-apks` |
| `ui/theme/NuxDesignTokens.kt` | Palet warna (`NuxColors`) + ukuran/shape (`NuxSizes`) | **Sumber tunggal warna.** Nama token lama dipertahankan |
| `ui/theme/FluxTheme.kt` | `FluxNuxTheme`: ColorScheme + Typography + Shapes M3 | Dipasang di `MainActivity` |
| `ui/theme/NuxResponsive.kt` | Skala DPI/font otomatis (`resp()`) | Jangan diubah tanpa alasan kuat |
| `ui/components/NuxComponents.kt` | `NuxCard`, `NuxButton`, `NuxBadge`, `NuxTextField`, `NuxDialog`, `FluxPill`, `FluxIconButton` | Dipakai hampir semua screen |
| `ui/components/NuxSidebar.kt` | Navigation rail + `NUX_NAV_ITEMS` | Tinggi penuh, lebar diatur Dashboard |
| `ui/screens/DashboardScreen.kt` | Shell aplikasi: rail, hero video, top bar, launch dock, routing tab + semua dialog | **File UI paling penting** |
| `ui/screens/AuthScreen.kt` | Login / register / aktivasi lisensi | 2 panel: branding + form |
| `ui/screens/AccountsScreen.kt` | Manajemen akun | |
| `ui/screens/ModsScreen.kt` | Modrinth browser + mod terpasang | |
| `ui/screens/AIScreen.kt` | Chat NUX AI | |
| `ui/screens/SettingsScreen.kt` | Profil + setting game/renderer | |
| `ui/screens/FriendsScreen.kt` | Sosial + voice room | |
| `ui/screens/CustomGuiEditorScreen.kt` | Editor kontrol in-game | Banyak warna hardcoded (belum disentuh) |
| `ui/dialogs/*.kt` | 19 dialog | Semua memakai `NuxDialog` → ikut tema otomatis |
| `ui/components/*.kt` | Avatar, skin, voice bar, loading overlay, markdown | |
| `res/values/themes.xml` | Window background Android | `#070A08` |

### Core (JANGAN diubah untuk tugas desain)

`core/account`, `core/auth`, `core/download`, `core/launch`, `core/instance`, `core/mods`, `core/renderer`,
`core/runtime`, `core/social`, `core/crash`, `core/settings`, `core/update`, `core/skin`, `core/recorder`.
Semua logika bisnis, jaringan, dan penyimpanan ada di sini.

---

## 3. Design System — "Flux Reactor"

Arah desain: **Material 3 dark, gaming HUD**. Bukan neon di mana-mana; kedalaman dari surface tonal,
aksen dari satu warna primary, dan detail "HUD" dari sudut terpotong + label monospace.

### Palet (`NuxColors`)

| Token | Hex | Peran M3 |
|---|---|---|
| `ForestGreen` | `#10C98A` | primary |
| `OnPrimary` | `#00291C` | onPrimary (teks di atas primary) |
| `MintGreen` | `#6CF2BE` | onPrimaryContainer / aksen teks |
| `SageGreen` | `#0A8F63` | inversePrimary |
| `SoftLime` | `#0A2C20` | primaryContainer |
| `LightGreen` | `#2910C98A` | glow 16% |
| `Background` | `#070A08` | background |
| `SurfaceLow` | `#0E1411` | surface / containerLow |
| `SurfaceWhite` | `#F20E1411` | kartu (surfaceContainerLow, 95%) |
| `SurfaceInput` | `#101813` | input field |
| `SurfaceElevated` | `#19231C` | containerHigh (chip, dock, rail item) |
| `SurfaceHigh` | `#1F2A22` | containerHighest / dialog |
| `Outline` | `#3E4C44` | outline |
| `OutlineVariant` | `#253029` | outlineVariant (garis kartu) |
| `CardBorder` | `#3335443A` | outline kartu |
| `DarkGray` | `#E9F3ED` | onSurface |
| `GrayNeutral` | `#9CAEA4` | onSurfaceVariant |
| `ErrorRed` | `#FF8C9A` | error |
| `Amber` | `#F7C36B` | tertiary / warning |

### Tipografi

- Type scale M3 di `FluxTheme.kt`. Display/headline pakai tracking negatif (rapat, tegas).
- `labelMedium` & `labelSmall` = **monospace** → dipakai untuk chip metadata (LOADER, JRE, versi).
  Ini yang memberi rasa "HUD" tanpa menambah ornamen.

### Bentuk

- Kartu 18–24dp, tombol 14dp, dialog 24dp, rail 28dp (ujung kanan).
- `NuxSizes.ShapeHudChip` = `CutCornerShape(2, 8, 2, 8)` untuk badge/chip metadata.

### Komponen wajib pakai ulang

| Kebutuhan | Pakai |
|---|---|
| Permukaan kartu | `NuxCard` |
| Tombol utama/sekunder | `NuxButton` |
| Label status kecil | `NuxBadge` |
| Input teks | `NuxTextField` |
| Dialog | `NuxDialog` |
| Pill top bar | `FluxPill` |
| Tombol ikon top bar | `FluxIconButton` |
| Navigasi utama | `NuxSidebar` (navigation rail) |

### Larangan visual (biar tidak slop)

- Jangan menumpuk `Brush.verticalGradient` + border gradient + inner border pada satu elemen.
- Maksimal **satu** aksen primary per komponen; sisanya surface tonal.
- Jangan tambah emoji di label UI. Jangan tambah glassmorphism baru.
- Jangan hardcode hex di screen/dialog baru — tambahkan token di `NuxDesignTokens.kt` kalau memang perlu.

---

## 4. Guardrail

1. **Hanya UI/UX.** Jangan sentuh `core/`, manifest permission, dependency, atau logika launch/download.
2. **Jangan rename** `NuxColors`/`NuxSizes`/komponen publik — ratusan call-site lain bergantung.
3. **Jangan hapus** dialog/tombol/fitur yang ada. Kalau tidak dipakai, biarkan.
4. **Teks tetap lewat `tr("key")` / `trStr("key", ...)`** — jangan ganti string jadi hardcoded English.
5. Semua ukuran sebaiknya dibungkus `resp()` agar skala DPI tetap jalan.
6. File besar (`DashboardScreen`, `ModsScreen`, `SettingsScreen`) lebih aman diedit dengan `str_replace` per blok.
7. Setelah perubahan: minimal cek import + pastikan tidak ada simbol yang hilang.

---

## 5. Change Log

Format: `ID | Tanggal | Area | File | Perubahan | Alasan`
(baris terbaru di atas)

| ID | Tanggal | Area | File | Perubahan | Alasan |
|---|---|---|---|---|---|
| FLX-012 | 2026-11-01 | Theme | `ui/theme/NuxDesignTokens.kt`, `ui/theme/FluxTheme.kt`, `res/values*/themes.xml` | Retoken `NuxColors` ke palet Material 3 Google (primary `#A8C7FA`, surface `#0F1014`) + type scale M3 baku + shapes baku; window bg `#0F1014` | Mengubah seluruh gaya UI/UX ke Material 3 standar Google tanpa rename token |
| FLX-011 | 2026-11-01 | Wallpaper | `core/wallpaper/WallpaperEngine.kt` (baru), `ui/components/FluxWallpaperView.kt` (baru), `DashboardScreen.kt`, `SettingsScreen.kt`, `LauncherSettings.kt`, `lang/*.json` | Wallpaper Minecraft otomatis dari internet (Wallhaven), fade antar gambar, interval 1–5 menit + section setting baru | Fitur ganti wallpaper otomatis yang diminta |
| FLX-010 | 2026-11-01 | OptiFine | `core/optifine/OptiFineService.kt` (baru), `MinecraftDownloader.kt`, `GameLauncher.kt`, `GameActivity.kt`, `MinecraftModels.kt`, `NuxAddInstanceDialog.kt`, `lang/*.json` | Opsi loader OptiFine di dialog Tambah Instance + patch headless `optifine.Patcher` via runtime bawaan (HMCL-style), launchwrapper otomatis, version json `inheritsFrom`, `--tweakClass` dikirim via `EXTRA_GAME_ARGS` | Dukungan OptiFine semua versi dengan unduhan otomatis |
| FLX-009b | 2026-11-01 | Perf | `GameActivity.kt` | Clamp `-Xmx` ke maks 85% RAM perangkat + log saat di-clamp | Jaga perangkat optimal tanpa menurunkan kualitas game |
| CI-004 | 2026-11-01 | CI | `.github/workflows/debug-build.yml`, `.github/workflows/release-build.yml` (baru), hapus `android-build.yml` | Build debug & release dipecah jadi dua workflow terpisah (artifact terpisah) | Debug & release tidak lagi digabung satu artifact |
| CI-003 | 2026-10-31 | CI | `.github/workflows/android-build.yml` | `TAG` nama APK di-sanitasi (`tr '/ ' '--'`) sebelum dipakai di path | Branch ber-`/` membuat `cp` gagal dengan *No such file or directory* |
| CI-002 | 2026-10-31 | CI | `.github/workflows/android-build.yml` | Paket SDK jadi `platforms;android-37.0` (fallback `android-37`) | Id `android-37` tidak ada di repo SDK → warning tiap build |
| CI-001 | 2026-10-31 | CI | `.github/workflows/android-build.yml` | Workflow build Android: debug+release, artifact 14 hari, publish ke GitHub Release saat tag `v*`; JDK 21, SDK 37, `chmod +x gradlew` | Build otomatis tanpa setup lokal |
| FLX-009 | 2026-10-31 | Auth | `ui/screens/AuthScreen.kt` | Rombak total alur login: hanya input Username tanpa akun/password/OTP/lisensi | Login instan langsung ke dashboard |
| FLX-008 | 2026-10-31 | Branding | `settings.gradle.kts`, `res/values/strings.xml`, `AndroidManifest.xml`, `lang/*.json`, `res/drawable/nux_icon.png`, `res/mipmap-*/` | Merombak nama aplikasi, manifest alias, i18n, serta icon/logo ke Flux Launcher | Transformasi identitas produk ke Flux Launcher |
| FLX-007 | 2026-10-31 | UI | `ui/components/NuxComponents.kt` | `FluxPill` & `FluxIconButton` tidak lagi pakai `clickable(indication=…)`; cukup `clickable(onClick=…)` | Overload itu wajib `interactionSource` → `compileDebugKotlin` gagal |
| FLX-006 | 2026-10-31 | UI | `ui/screens/AuthScreen.kt` | Panel pakai `SurfaceLow` + `OutlineVariant`, segmented control pakai `Background`/`SurfaceHigh`/`Outline`, CTA pakai `OnPrimary` | Menghilangkan gradient ganda, kontras teks CTA naik |
| FLX-005 | 2026-10-31 | UI | `ui/screens/DashboardScreen.kt` | Frame hijau → `Background`; rail 82dp; kartu utama `SurfaceLow` + 1dp outline; top bar & launch dock jadi surface tonal (bukan gradient); chip loader/versi/JRE pakai `SurfaceHigh` + outline | Konsistensi M3, mengurangi ornamen |
| FLX-004 | 2026-10-31 | UI | `ui/components/NuxSidebar.kt` | Sidebar → navigation rail M3 (indikator pill, label selalu tampil, divider About) | Navigasi lebih jelas & sesuai M3 |
| FLX-003 | 2026-10-31 | UI | `ui/components/NuxComponents.kt` | `NuxCard`/`NuxButton`/`NuxBadge`/`NuxTextField`/`NuxDialog` ditulis ulang M3; tambah `FluxPill`, `FluxIconButton` | Satu bahasa visual untuk semua screen |
| FLX-002 | 2026-10-31 | Tema | `ui/theme/FluxTheme.kt` (baru), `MainActivity.kt` | `FluxNuxTheme` (ColorScheme + Typography + Shapes M3) dipasang di root | Komponen M3 otomatis ikut tema |
| FLX-001 | 2026-10-31 | Tema | `ui/theme/NuxDesignTokens.kt`, `res/values/themes.xml`, `res/values-v28/themes.xml` | Palet "Reactor Graphite" + HUD chip shape; window background `#070A08` | Warna dasar baru, nama token lama dipertahankan |
| DOC-001 | 2026-10-31 | Docs | `README.md`, `AI_NOTES.md` | README ditulis ulang untuk FluxLauncher; file ini dibuat | Branding repo + catatan kerja AI |

---

## 6. Status Verifikasi

- `./gradlew compileDebugKotlin` **lulus** (JDK 21 + SDK `platforms;android-37.0` + build-tools 36).
- Sisa output build hanya warning deprecation lama (`Icons.*`, `Locale`), bukan error.
- Reproduksi lokal cepat: JDK 21 + `ANDROID_HOME` berisi `platforms;android-37.0`, lalu `bash gradlew compileDebugKotlin`.
- ⚠️ Fitur OptiFine & wallpaper (perangkat nyata) belum diuji manual — alur sisip berpatokan
  resep HMCL/xmcl (`optifine.Patcher` + `launchwrapper-of.txt`) dan jalur BMCLAPI.

---

## 7. Backlog UI (belum dikerjakan)

- `CustomGuiEditorScreen.kt`: 172 warna hardcoded — perlu migrasi token (paling besar).
- `NuxCrashDialog`, `NuxAddInstanceDialog`, `ModsScreen`: masih banyak gradient/border manual.
- `GameLoadingOverlay` + `FloatingVoiceBar`: belum diselaraskan ke surface M3.
- Empty state & skeleton loading belum seragam di semua screen.
