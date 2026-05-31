# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Debug APK
./gradlew assembleRelease        # Release APK (R8 minified)
./gradlew installDebug           # Build + install
./gradlew test                   # Unit tests (placeholder only)
./gradlew lint
```

## Architecture

Single-module Android app. Hybrid XML+Compose architecture with modern Android stack.

**Tech stack**: Kotlin 2.0.0, compileSdk 34, minSdk 24, Gradle 8.7, AGP 8.5.2, Java 17

**Build system**: Gradle Kotlin DSL with Version Catalog (`gradle/libs.versions.toml`), Compose compiler plugin, Kotlin Serialization plugin, R8 enabled

**DI**: Hilt 2.51.1. Modules: `AppModule` (Configurations, Room DB, DAOs, Gson, storage dir), `NetworkModule` (OkHttpClient, BasicService)

**Data layer**: Room database for favorites (`JaviewerDatabase`). DataStore for config. `ConfigRepository` bridges Room/DataStore. Network: Retrofit `suspend fun` → Jsoup HTML parsing. Avgle/PSVS use kotlinx.serialization.

**UI**: Compose with Material 3 (`ui/theme/`), Navigation Compose (`ui/navigation/`). `ui/MainActivity.kt` (Compose) coexists with legacy XML Activities. `VideoPlayerActivity` uses Media3 ExoPlayer.

**Coroutines/Flow**: All Retrofit interfaces are `suspend fun`. ViewModels use `StateFlow<UiState<T>>`.

## Key Packages

| Package | Role |
|---|---|
| `ui/` | Compose theme, navigation, screens, components, MainActivity |
| `data/` | Room DB, DAOs, DataStore, migration |
| `viewmodel/` | All use StateFlow |
| `repository/` | MovieRepository, ActressRepository (suspend), ConfigRepository (Room+DataStore), PropertiesRepository, DataSourceRepository |
| `activity/` | Legacy XML Activities + VideoPlayerActivity (Media3) |
| `fragment/` | Legacy XML Fragments |
| `di/` | AppModule, NetworkModule |
| `util/` | Media3PlayerImpl, UiState |

## Notes

- ApplicationId: `io.github.javiewer`, version `3.0.0-kotlin`
- Jetifier enabled (MaterialDrawer 6.x, AHBottomNavigation)
- Coil removed; using Glide in Compose via AndroidView
- Gson for Configurations/Properties; kotlinx.serialization for Avgle/PSVS
- `JAViewer.kt` holds `SERVICE`, `CONFIGURATIONS`, `DATA_SOURCES` for legacy code
