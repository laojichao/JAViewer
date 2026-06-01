package io.github.javiewer.network

import io.github.javiewer.JAViewer
import io.github.javiewer.network.item.AvgleSearchResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Avgle 视频搜索 API 服务接口。
 *
 * 用于搜索影片预览视频，返回 JSON 格式的 [AvgleSearchResult]。
 * 使用 kotlinx.serialization 进行 JSON 反序列化。
 *
 * 通过 [INSTANCE] 获取自构建的 Retrofit 单例。
 */
interface Avgle {

    /**
     * 搜索影片预览视频。
     *
     * @param keyword 搜索关键词（通常是影片编号）
     * @return 搜索结果，包含视频预览信息
     */
    @GET("/v1/search/{keyword}/0?limit=1")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String): AvgleSearchResult

    /**
     * 获取 Avgle 路径下的原始内容。
     *
     * @param path 资源路径
     * @return 原始响应体
     */
    @GET("/{path}")
    suspend fun get(@Path("path") path: String): ResponseBody

    companion object {
        const val BASE_URL = "https://api.avgle.com"

        private val json = Json { ignoreUnknownKeys = true }

        /** 自构建的 Retrofit 单例 */
        val INSTANCE: Avgle = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(Avgle::class.java)
    }
}
