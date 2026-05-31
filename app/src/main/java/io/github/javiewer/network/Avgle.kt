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

interface Avgle {

    @GET("/v1/search/{keyword}/0?limit=1")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String): AvgleSearchResult

    @GET("/{path}")
    suspend fun get(@Path("path") path: String): ResponseBody

    companion object {
        const val BASE_URL = "https://api.avgle.com"

        private val json = Json { ignoreUnknownKeys = true }

        val INSTANCE: Avgle = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(Avgle::class.java)
    }
}
