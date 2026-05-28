package io.github.javiewer.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface BasicService {

    @GET("/cn/page/{page}")
    fun getHomePage(@Path("page") page: Int): Call<ResponseBody>

    @GET("/cn/released/page/{page}")
    fun getReleased(@Path("page") page: Int): Call<ResponseBody>

    @GET("/cn/popular/page/{page}")
    fun getPopular(@Path("page") page: Int): Call<ResponseBody>

    @GET("/cn/actresses/page/{page}")
    fun getActresses(@Path("page") page: Int): Call<ResponseBody>

    @GET("/cn/genre")
    fun getGenre(): Call<ResponseBody>

    @GET
    fun get(@Url url: String): Call<ResponseBody>

    companion object {
        const val LANGUAGE_NODE = "/cn"
    }
}
