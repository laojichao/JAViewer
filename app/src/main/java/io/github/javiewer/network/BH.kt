package io.github.javiewer.network

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface BH {

    @GET("/so/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun search(@Path("keyword") keyword: String, @Path("page") page: Int): ResponseBody

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun get(@Url url: String): ResponseBody

    @GET("/index/search.html")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    suspend fun searchByQuery(@Query("keyword") keyword: String): ResponseBody

    companion object {
        const val BASE_URL = "https://baihu7.xyz"

        val INSTANCE: BH = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .build()
            .create(BH::class.java)
    }
}
