package io.github.javiewer.network

import io.github.javiewer.JAViewer
import io.github.javiewer.network.item.AvgleSearchResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface Avgle {

    @GET("/v1/search/{keyword}/0?limit=1")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    fun search(@Path("keyword") keyword: String): Call<AvgleSearchResult>

    @GET("/{path}")
    fun get(@Path("path") path: String): Call<ResponseBody>

    companion object {
        const val BASE_URL = "https://api.avgle.com"

        val INSTANCE: Avgle = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Avgle::class.java)
    }
}
