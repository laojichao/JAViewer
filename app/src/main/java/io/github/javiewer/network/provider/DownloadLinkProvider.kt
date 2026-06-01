package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import okhttp3.ResponseBody

/**
 * 下载链接提供者的抽象基类，定义种子搜索站点的统一接口。
 *
 * 每个具体实现对应一个种子搜索站点（BTSO、TorrentKitty、BH、BTMOVI），
 * 通过 [getProvider] 工厂方法根据名称获取对应实例。
 *
 * 子类需实现 [search]、[parseDownloadLinks]、[get]、[parseMagnetLink] 四个方法，
 * 其中 [search] 和 [get] 为挂起函数（网络请求），[parseDownloadLinks] 和 [parseMagnetLink] 为同步函数（HTML 解析）。
 */
abstract class DownloadLinkProvider {

    /**
     * 搜索种子资源。
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @return 搜索结果页面 HTML，不支持该页码时返回 null
     */
    abstract suspend fun search(keyword: String, page: Int): ResponseBody?

    /**
     * 解析搜索结果 HTML，提取下载链接列表。
     *
     * @param htmlContent 搜索结果页面 HTML
     * @return 解析后的下载链接列表
     */
    abstract fun parseDownloadLinks(htmlContent: String): List<DownloadLink>

    /**
     * 获取资源详情页面。
     *
     * @param url 详情页 URL
     * @return 详情页 HTML，不支持时返回 null
     */
    abstract suspend fun get(url: String): ResponseBody?

    /**
     * 解析详情页 HTML，提取磁力链接。
     *
     * @param htmlContent 详情页 HTML
     * @return 解析后的 [MagnetLink]，未找到时返回 null
     */
    abstract fun parseMagnetLink(htmlContent: String): MagnetLink?

    companion object {
        /**
         * 根据名称获取对应的下载链接提供者。
         *
         * @param name 提供者名称（不区分大小写）：btso、torrentkitty、bh、btmovi
         * @return 对应的 [DownloadLinkProvider] 实例，未知名称返回 null
         */
        @JvmStatic
        fun getProvider(name: String): DownloadLinkProvider? {
            return when (name.lowercase().trim()) {
                "btso" -> BTSOLinkProvider()
                "torrentkitty" -> TorrentKittyLinkProvider()
                "bh" -> BHLinkProvider()
                "btmovi" -> BTMOVILinkProvider()
                else -> null
            }
        }
    }
}
