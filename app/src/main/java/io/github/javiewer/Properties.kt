package io.github.javiewer

import io.github.javiewer.adapter.item.DataSource

data class Properties(
    val latest_version: String? = null,
    val latest_version_code: Int = 0,
    val changelog: String? = null,
    val data_sources: List<DataSource>? = null
) {
    fun getLatestVersion(): String? = latest_version
    fun getLatestVersionCode(): Int = latest_version_code
    fun getDataSources(): List<DataSource> = data_sources ?: emptyList()
    fun getChangelog(): String? = changelog
}
