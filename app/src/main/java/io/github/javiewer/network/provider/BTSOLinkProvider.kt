package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.network.BTSO
import okhttp3.ResponseBody
import org.jsoup.Jsoup

/**
 * BTSO 种子搜索站点的下载链接提供者实现。
 *
 * 解析 BTSO 搜索结果页面，提取文件名、大小、日期和详情链接。
 * 磁力链接需从详情页中提取（class="magnet-link"）。
 */
class BTSOLinkProvider : DownloadLinkProvider() {

    override suspend fun search(keyword: String, page: Int): ResponseBody {
        return BTSO.INSTANCE.search(keyword, page)
    }

    override fun parseDownloadLinks(htmlContent: String): List<DownloadLink> {
        val links = mutableListOf<DownloadLink>()
        val rows = Jsoup.parse(htmlContent).getElementsByClass("row")
        for (row in rows) {
            try {
                val a = row.getElementsByTag("a").first() ?: continue
                links.add(
                    DownloadLink.create(
                        row.getElementsByClass("file").first()?.text() ?: "",
                        row.getElementsByClass("size").first()?.text() ?: "",
                        row.getElementsByClass("date").first()?.text() ?: "",
                        a.attr("href"),
                        null
                    )
                )
            } catch (_: Exception) {
            }
        }
        return links
    }

    override suspend fun get(url: String): ResponseBody {
        return BTSO.INSTANCE.get(url)
    }

    override fun parseMagnetLink(htmlContent: String): MagnetLink {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementsByClass("magnet-link").first()?.text())
    }
}
