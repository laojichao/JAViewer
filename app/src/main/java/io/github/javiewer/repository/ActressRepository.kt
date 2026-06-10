package io.github.javiewer.repository

import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.network.BasicService
import io.github.javiewer.network.provider.AVMOProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * 女优数据仓库，封装 [BasicService] 的网络请求和 [AVMOProvider] 的 HTML 解析。
 * 使用 [Provider] 注入以确保每次请求使用当前数据源的 URL。
 *
 * @property serviceProvider BasicService 提供者
 */
@Singleton
class ActressRepository @Inject constructor(
    private val serviceProvider: Provider<BasicService>
) {
    private val service get() = serviceProvider.get()
    /**
     * 获取女优列表。
     *
     * @param page 页码，从 1 开始
     * @return 当前页的女优列表
     */
    suspend fun getActresses(page: Int): List<Actress> {
        val html = service.getActresses(page).string()
        return AVMOProvider.parseActresses(html)
    }
}
