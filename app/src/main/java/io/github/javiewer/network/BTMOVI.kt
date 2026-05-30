package io.github.javiewer.network

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

interface BTMOVI {

    @GET("/so/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    fun search(@Path("keyword") keyword: String, @Path("page") page: Int): Call<ResponseBody>

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    fun get(@Url url: String): Call<ResponseBody>

    @GET("/so/{keyword}.html")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    fun searchSingle(@Path("keyword") keyword: String): Call<ResponseBody>

    companion object {
        const val BASE_URL = "https://btmovi.space"

        val INSTANCE: BTMOVI = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .build()
            .create(BTMOVI::class.java)
    }
}
