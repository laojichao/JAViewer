package io.github.javiewer.repository

import io.github.javiewer.adapter.item.DataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSourceRepository @Inject constructor() {

    private val _dataSources = MutableStateFlow<List<DataSource>>(emptyList())
    val dataSources: StateFlow<List<DataSource>> = _dataSources

    private val _hostReplacements = MutableStateFlow<Map<String, String>>(emptyMap())
    val hostReplacements: StateFlow<Map<String, String>> = _hostReplacements

    fun setDataSources(sources: List<DataSource>) {
        _dataSources.value = sources
        val replacements = mutableMapOf<String, String>()
        for (source in sources) {
            try {
                val host = URI(source.link).host
                source.legacies?.forEach { legacy ->
                    replacements[legacy] = host
                }
            } catch (_: URISyntaxException) {
            }
        }
        _hostReplacements.value = replacements
    }

    fun getDataSources(): List<DataSource> = _dataSources.value

    fun getHostReplacements(): Map<String, String> = _hostReplacements.value
}
