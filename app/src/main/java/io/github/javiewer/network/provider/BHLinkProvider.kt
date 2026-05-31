package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.network.BH
import okhttp3.ResponseBody
import org.jsoup.Jsoup

class BHLinkProvider : DownloadLinkProvider() {

    override suspend fun search(keyword: String, page: Int): ResponseBody? {
        return if (page == 1) BH.INSTANCE.searchByQuery(keyword) else null
    }

    override fun parseDownloadLinks(htmlContent: String): List<DownloadLink> {
        val links = mutableListOf<DownloadLink>()
        val rows = Jsoup.parse(htmlContent).getElementsByClass("layui-colla-item fly-box")
        for (row in rows) {
            try {
                val a = row.getElementsByClass("layui-colla-item").first()?.getElementsByTag("a")?.first() ?: continue
                val magnet = a.attr("href")
                links.add(
                    DownloadLink.create(
                        getTitle(row),
                        row.getElementsByClass("layui-colla-content layui-show").first()?.getElementsByTag("p")?.get(3)?.text() ?: "",
                        row.getElementsByClass("layui-colla-content layui-show").first()?.getElementsByTag("p")?.get(2)?.text() ?: "",
                        null,
                        magnet
                    )
                )
            } catch (_: Exception) {
            }
        }
        return links
    }

    override suspend fun get(url: String): ResponseBody? = null

    override fun parseMagnetLink(htmlContent: String): MagnetLink? = null

    private fun getTitle(e: org.jsoup.nodes.Element): String {
        val font = e.getElementsByClass("layui-colla-title search-colla-title").first()?.getElementsByTag("font")?.text()
        if (!font.isNullOrEmpty()) return font
        val h2 = e.getElementsByTag("h2").first()?.text() ?: return ""
        val spaceIdx = h2.indexOf(" ")
        return if (spaceIdx >= 0) h2.substring(spaceIdx) else h2
    }
}
