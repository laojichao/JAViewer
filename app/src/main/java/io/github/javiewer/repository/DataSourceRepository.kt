package io.github.javiewer.repository

import io.github.javiewer.adapter.item.DataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据源仓库，管理可用数据源列表和域名重写映射。
 *
 * 从远程 `properties.json` 加载数据源后，自动提取各数据源的历史域名（[DataSource.legacies]），
 * 生成域名重写映射表供 OkHttp 拦截器使用，确保旧域名请求能正确转发到当前域名。
 *
 * @property dataSources 可用数据源列表的响应式数据流
 * @property hostReplacements 域名重写映射表（旧域名 -> 当前域名）的响应式数据流
 */
@Singleton
class DataSourceRepository @Inject constructor() {

    private val _dataSources = MutableStateFlow<List<DataSource>>(emptyList())
    val dataSources: StateFlow<List<DataSource>> = _dataSources

    private val _hostReplacements = MutableStateFlow<Map<String, String>>(emptyMap())
    val hostReplacements: StateFlow<Map<String, String>> = _hostReplacements

    /**
     * 设置数据源列表并自动构建域名重写映射。
     *
     * @param sources 从远程配置加载的数据源列表
     */
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

    /** 获取当前数据源列表 */
    fun getDataSources(): List<DataSource> = _dataSources.value

    /** 获取域名重写映射表 */
    fun getHostReplacements(): Map<String, String> = _hostReplacements.value
}
