package io.github.javiewer.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * AVMOO 主站 Retrofit 服务接口。
 *
 * 所有方法均为挂起函数，返回原始 HTML [ResponseBody]，
 * 需由 [io.github.javiewer.network.provider.AVMOProvider] 解析为领域模型。
 * 基础 URL 由当前 [io.github.javiewer.adapter.item.DataSource] 动态决定。
 */
interface BasicService {

    /**
     * 获取首页影片列表 HTML。
     *
     * @param page 页码，从 1 开始
     * @return 页面 HTML 内容
     */
    @GET("/cn/page/{page}")
    suspend fun getHomePage(@Path("page") page: Int): ResponseBody

    /**
     * 获取最新发布影片列表 HTML。
     *
     * @param page 页码
     * @return 页面 HTML 内容
     */
    @GET("/cn/released/page/{page}")
    suspend fun getReleased(@Path("page") page: Int): ResponseBody

    /**
     * 获取热门影片列表 HTML。
     *
     * @param page 页码
     * @return 页面 HTML 内容
     */
    @GET("/cn/popular/page/{page}")
    suspend fun getPopular(@Path("page") page: Int): ResponseBody

    /**
     * 获取女优列表 HTML。
     *
     * @param page 页码
     * @return 页面 HTML 内容
     */
    @GET("/cn/actresses/page/{page}")
    suspend fun getActresses(@Path("page") page: Int): ResponseBody

    /**
     * 获取类别页面 HTML。
     *
     * @return 类别页面 HTML 内容
     */
    @GET("/cn/genre")
    suspend fun getGenre(): ResponseBody

    /**
     * 获取指定 URL 的页面 HTML（通用方法）。
     *
     * @param url 完整的页面 URL
     * @return 页面 HTML 内容
     */
    @GET
    suspend fun get(@Url url: String): ResponseBody

    companion object {
        /** 语言路径节点，所有页面 URL 的前缀 */
        const val LANGUAGE_NODE = "/cn"
    }
}
