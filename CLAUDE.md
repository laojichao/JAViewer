# CLAUDE.md - JAViewer

## 项目概述
JAV 查看器应用，基于质感设计（Material Design），原版 2.0.0-Alpha-1 修改而来，当前版本 `3.0.0-kotlin`。提供流畅的浏览体验，支持多维度内容展示，集成视频播放功能。包名为 `io.github.javiewer`，口号："质感设计，更优雅的驾车体验"。

## 技术栈
- 语言：Kotlin 2.0.0
- UI 框架：Jetpack Compose (Material 3) + 遗留 XML 布局混合架构
- 依赖注入：Hilt 2.51.1
- 序列化：Kotlin Serialization（Avgle/PSVS 数据）、Gson（Configurations/Properties）
- 数据库：Room（收藏夹 `JaviewerDatabase`）
- 配置存储：DataStore
- 网络请求：Retrofit + Jsoup HTML 解析
- 视频播放：Media3 ExoPlayer
- 图片加载：Glide（通过 AndroidView 在 Compose 中使用）
- 异步处理：Kotlin Coroutines + Flow
- 其他：kapt、kotlin-parcelize、Jetifier
- 版本管理：Version Catalog（`gradle/libs.versions.toml`）

## 构建信息
- compileSdk：34
- minSdk：24
- Gradle：8.7
- AGP：8.5.2
- Java：17
- R8：已启用
- 构建命令：
  - `./gradlew assembleDebug` — Debug APK
  - `./gradlew assembleRelease` — Release APK（R8 混淆）
  - `./gradlew installDebug` — 构建并安装
  - `./gradlew test` — 单元测试
  - `./gradlew lint` — 代码检查

## 关键模块/类

| 包名 | 职责 |
|---|---|
| `ui/` | Compose 主题、导航、页面、组件、MainActivity |
| `data/` | Room 数据库、DAO、DataStore、数据库迁移 |
| `viewmodel/` | ViewModel 层，均使用 StateFlow 管理状态 |
| `repository/` | MovieRepository、ActressRepository（suspend）、ConfigRepository（Room+DataStore）、PropertiesRepository、DataSourceRepository |
| `activity/` | 遗留 XML Activity + VideoPlayerActivity（Media3） |
| `fragment/` | 遗留 XML Fragment |
| `di/` | Hilt 模块：AppModule（Configurations、Room DB、DAO、Gson、存储目录）、NetworkModule（OkHttpClient、BasicService） |
| `util/` | Media3PlayerImpl、UiState |

## 开发注意事项
- 架构为 Compose + 遗留 XML 混合，新功能建议使用 Compose，避免新增 XML Activity/Fragment
- Hilt DI 模块：`AppModule` 提供数据层依赖，`NetworkModule` 提供网络层依赖
- 所有 Retrofit 接口均为 `suspend fun`，ViewModel 使用 `StateFlow<UiState<T>>` 管理状态
- Jetifier 仍需启用（MaterialDrawer 6.x、AHBottomNavigation 依赖旧版 Support 库）
- Coil 已移除，图片加载使用 Glide，通过 `AndroidView` 桥接在 Compose 中使用
- Gson 用于 Configurations/Properties 序列化，kotlinx.serialization 用于 Avgle/PSVS 数据
- `JAViewer.kt` 中持有 `SERVICE`、`CONFIGURATIONS`、`DATA_SOURCES` 全局引用，供遗留代码使用
- Room 数据库需注意版本迁移策略，避免升级丢失用户收藏数据
- Media3 ExoPlayer 视频播放集成在 `VideoPlayerActivity` 中，注意播放器生命周期管理
