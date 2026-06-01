package io.github.javiewer.network

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * TorrentKitty 种子搜索站点 Retrofit 服务接口。
 *
 * 由 [io.github.javiewer.network.provider.TorrentKittyLinkProvider] 使用。
 * 仅支持第一页搜索，不支持分页。
 *
 * 通过 [INSTANCE] 获取自构建的 Retrofit 单例。
 */
interface TorrentKitty {

    /**
     * 搜索种子资源（仅第一页）。
     *
     * @param keyword 搜索关键词
     * @return 搜索结果页面 HTML
     */
    @GET("/search/{keyword}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String): ResponseBody

    /**
     * 获取指定 URL 的页面内容。
     *
     * @param url 完整的页面 URL
     * @return 页面 HTML 内容
     */
    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun get(@Url url: String): ResponseBody

    companion object {
        const val BASE_URL = "https://www.torrentkitty.tv"

        /** 自构建的 Retrofit 单例 */
        val INSTANCE: TorrentKitty = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .build()
            .create(TorrentKitty::class.java)
    }
}
