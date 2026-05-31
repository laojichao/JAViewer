package io.github.javiewer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.javiewer.JAViewer
import io.github.javiewer.network.BasicService
import io.github.javiewer.repository.DataSourceRepository
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(dataSourceRepository: DataSourceRepository): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val replacements = dataSourceRepository.getHostReplacements()
                val host = original.url.host
                val replacement = replacements[host]
                val url = if (replacement != null) {
                    original.url.newBuilder().host(replacement).build()
                } else {
                    original.url
                }
                val request = original.newBuilder()
                    .url(url)
                    .header("User-Agent", JAViewer.USER_AGENT)
                    .build()
                chain.proceed(request)
            })
            .cookieJar(object : CookieJar {
                private val cookieStore = java.util.concurrent.ConcurrentHashMap<HttpUrl, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url] ?: emptyList()
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideBasicService(okHttpClient: OkHttpClient): BasicService {
        val link = JAViewer.getDataSource().link ?: "https://avos.pw"
        return retrofit2.Retrofit.Builder()
            .baseUrl(link)
            .client(okHttpClient)
            .build()
            .create(BasicService::class.java)
    }
}
