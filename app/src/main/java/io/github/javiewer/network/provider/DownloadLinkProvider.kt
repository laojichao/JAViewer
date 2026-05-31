package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import okhttp3.ResponseBody

abstract class DownloadLinkProvider {

    abstract suspend fun search(keyword: String, page: Int): ResponseBody?

    abstract fun parseDownloadLinks(htmlContent: String): List<DownloadLink>

    abstract suspend fun get(url: String): ResponseBody?

    abstract fun parseMagnetLink(htmlContent: String): MagnetLink?

    companion object {
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
