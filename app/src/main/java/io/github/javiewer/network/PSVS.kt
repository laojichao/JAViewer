package io.github.javiewer.network

import io.github.javiewer.JAViewer
import io.github.javiewer.network.item.AvgleSearchResult
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface PSVS {

    @GET("/psvs/search.php")
    suspend fun search(@Query("kw") keyword: String): AvgleSearchResult

    companion object {
        const val BASE_URL = "https://api.rekonquer.com"

        private val json = Json { ignoreUnknownKeys = true }

        val INSTANCE: PSVS = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(JAViewer.httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PSVS::class.java)
    }
}
