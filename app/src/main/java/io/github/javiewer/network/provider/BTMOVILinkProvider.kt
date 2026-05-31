package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.network.BTMOVI
import okhttp3.ResponseBody
import org.jsoup.Jsoup

class BTMOVILinkProvider : DownloadLinkProvider() {

    override suspend fun search(keyword: String, page: Int): ResponseBody? {
        return if (page == 1) BTMOVI.INSTANCE.searchSingle(keyword) else null
    }

    override fun parseDownloadLinks(htmlContent: String): List<DownloadLink> {
        val links = mutableListOf<DownloadLink>()
        val rows = Jsoup.parse(htmlContent).getElementsByClass("search-item")
        for (row in rows) {
            try {
                val a = row.getElementsByTag("a").first() ?: continue
                val url = BTMOVI.BASE_URL + a.attr("href")
                links.add(
                    DownloadLink.create(
                        row.getElementsByClass("item-title").first()?.text() ?: "",
                        row.getElementsByClass("cpill yellow-pill").first()?.text() ?: "",
                        row.getElementsByClass("item-bar").first()?.getElementsByTag("b")?.first()?.text() ?: "",
                        url,
                        null
                    )
                )
            } catch (_: Exception) {
            }
        }
        return links
    }

    override suspend fun get(url: String): ResponseBody {
        return BTMOVI.INSTANCE.get(url)
    }

    override fun parseMagnetLink(htmlContent: String): MagnetLink {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementById("down-url")?.attr("href"))
    }
}
