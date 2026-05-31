package io.github.javiewer.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface BasicService {

    @GET("/cn/page/{page}")
    suspend fun getHomePage(@Path("page") page: Int): ResponseBody

    @GET("/cn/released/page/{page}")
    suspend fun getReleased(@Path("page") page: Int): ResponseBody

    @GET("/cn/popular/page/{page}")
    suspend fun getPopular(@Path("page") page: Int): ResponseBody

    @GET("/cn/actresses/page/{page}")
    suspend fun getActresses(@Path("page") page: Int): ResponseBody

    @GET("/cn/genre")
    suspend fun getGenre(): ResponseBody

    @GET
    suspend fun get(@Url url: String): ResponseBody

    companion object {
        const val LANGUAGE_NODE = "/cn"
    }
}
