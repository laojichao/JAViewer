package io.github.javiewer

import io.github.javiewer.adapter.item.DataSource

/**
 * 远程配置文件数据模型，对应 `properties.json` 的 JSON 结构。
 *
 * 应用启动时从 GitHub 远程获取，失败时回退到本地 assets 中的备份。
 * 用于检查应用更新、获取可用数据源列表及域名重写映射。
 *
 * @property latest_version 最新版本号字符串（如 "3.0.1"）
 * @property latest_version_code 最新版本号（整数），用于与当前版本比较
 * @property changelog 更新日志内容
 * @property data_sources 可用数据源列表，为 null 时使用默认数据源
 */
data class Properties(
    val latest_version: String? = null,
    val latest_version_code: Int = 0,
    val changelog: String? = null,
    val data_sources: List<DataSource>? = null
) {
    /** 获取最新版本号字符串 */
    fun getLatestVersion(): String? = latest_version

    /** 获取最新版本号整数值 */
    fun getLatestVersionCode(): Int = latest_version_code

    /** 获取可用数据源列表，为 null 时返回空列表 */
    fun getDataSources(): List<DataSource> = data_sources ?: emptyList()
}
