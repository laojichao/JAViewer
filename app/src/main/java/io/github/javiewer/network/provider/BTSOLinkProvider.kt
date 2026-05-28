package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.network.BTSO
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call

class BTSOLinkProvider : DownloadLinkProvider() {

    override fun search(keyword: String, page: Int): Call<ResponseBody> {
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

    override fun get(url: String): Call<ResponseBody> {
        return BTSO.INSTANCE.get(url)
    }

    override fun parseMagnetLink(htmlContent: String): MagnetLink {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementsByClass("magnet-link").first()?.text())
    }
}
