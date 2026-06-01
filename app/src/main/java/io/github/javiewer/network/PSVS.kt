package io.github.javiewer.network

import io.github.javiewer.JAViewer
import io.github.javiewer.network.item.AvgleSearchResult
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * PSVS 视频搜索 API 服务接口。
 *
 * 功能与 [Avgle] 类似，使用不同的 API 端点搜索影片在线视频源。
 * 返回的 JSON 结构与 Avgle 兼容，共用 [AvgleSearchResult] 模型。
 *
 * 通过 [INSTANCE] 获取自构建的 Retrofit 单例。
 */
interface PSVS {

    /**
     * 搜索影片在线视频源。
     *
     * @param keyword 搜索关键词（通常是影片编号）
     * @return 搜索结果，包含视频播放信息
     */
    @GET("/psvs/search.php")
    suspend fun search(@Query("kw") keyword: String): AvgleSearchResult

    companion object {
        const val BASE_URL = "https://api.rekonquer.com"

        private val json = Json { ignoreUnknownKeys = true }

        /** 自构建的 Retrofit 单例 */
        val INSTANCE: PSVS = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PSVS::class.java)
    }
}
