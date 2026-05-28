package io.github.javiewer.network

import io.github.javiewer.network.item.AvgleSearchResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PSVS {

    @GET("/psvs/search.php")
    fun search(@Query("kw") keyword: String): Call<AvgleSearchResult>

    companion object {
        const val BASE_URL = "http://api.rekonquer.com"

        val INSTANCE: PSVS = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PSVS::class.java)
    }
}
