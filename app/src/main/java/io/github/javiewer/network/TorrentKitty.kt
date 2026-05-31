package io.github.javiewer.network

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

interface TorrentKitty {

    @GET("/search/{keyword}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String): ResponseBody

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun get(@Url url: String): ResponseBody

    companion object {
        const val BASE_URL = "https://www.torrentkitty.tv"

        val INSTANCE: TorrentKitty = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .build()
            .create(TorrentKitty::class.java)
    }
}
