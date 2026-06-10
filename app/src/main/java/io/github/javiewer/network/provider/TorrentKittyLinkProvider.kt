package io.github.javiewer.network.provider

import android.util.Log
import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.network.TorrentKitty
import okhttp3.ResponseBody
import org.jsoup.Jsoup

/**
 * TorrentKitty 种子搜索站点的下载链接提供者实现。
 *
 * 仅支持第一页搜索（不分页）。磁力链接直接从搜索结果中提取
 * （rel="magnet" 属性），无需额外请求详情页。
 */
class TorrentKittyLinkProvider : DownloadLinkProvider() {

    override suspend fun search(keyword: String, page: Int): ResponseBody? {
        return if (page == 1) TorrentKitty.INSTANCE.search(keyword) else null
    }

    override fun parseDownloadLinks(htmlContent: String): List<DownloadLink> {
        val links = mutableListOf<DownloadLink>()
        val table = Jsoup.parse(htmlContent).getElementById("archiveResult") ?: return links
        for (tr in table.getElementsByTag("tr")) {
            try {
                links.add(
                    DownloadLink.create(
                        tr.getElementsByClass("name").first()?.text() ?: "",
                        "",
                        tr.getElementsByClass("date").first()?.text() ?: "",
                        null,
                        tr.getElementsByAttributeValue("rel", "magnet").first()?.attr("href")
                    )
                )
            } catch (e: Exception) {
                Log.w("TorrentKittyLinkProvider", "Failed to parse row", e)
            }
        }
        return links
    }

    override suspend fun get(url: String): ResponseBody? = null

    override fun parseMagnetLink(htmlContent: String): MagnetLink? = null
}
