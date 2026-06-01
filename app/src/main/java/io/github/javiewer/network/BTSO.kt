package io.github.javiewer.network

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * BTSO 种子搜索站点 Retrofit 服务接口。
 *
 * 用于搜索和获取种子资源页面 HTML，由 [io.github.javiewer.network.provider.BTSOLinkProvider] 解析。
 *
 * 通过 [INSTANCE] 获取自构建的 Retrofit 单例。
 */
interface BTSO {

    /**
     * 搜索种子资源。
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @return 搜索结果页面 HTML
     */
    @GET("/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String, @Path("page") page: Int): ResponseBody

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
        const val BASE_URL = "https://btsow.com"

        /** 自构建的 Retrofit 单例 */
        val INSTANCE: BTSO = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .build()
            .create(BTSO::class.java)
    }
}
