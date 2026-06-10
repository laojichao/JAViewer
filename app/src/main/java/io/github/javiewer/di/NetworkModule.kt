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

/**
 * Hilt 网络依赖模块，提供 [OkHttpClient] 和 [BasicService] 单例。
 *
 * OkHttpClient 包含域名重写拦截器（将旧域名请求转发到当前域名）
 * 和内存 Cookie 管理器。
 *
 * BasicService 基于当前数据源的 URL 创建，数据源切换后需重建。
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供配置了域名重写拦截器的 OkHttpClient。
     *
     * 拦截器根据 [DataSourceRepository.getHostReplacements] 映射表
     * 将旧域名请求自动转发到当前域名，确保历史 URL 仍然可用。
     */
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

    /**
     * 提供 [BasicService] Retrofit 实例。
     * 每次注入时基于当前数据源 URL 创建新实例，确保数据源切换后 URL 正确。
     * 不使用 @Singleton，因为数据源 URL 可能在运行时变更。
     */
    @Provides
    fun provideBasicService(okHttpClient: OkHttpClient): BasicService {
        val link = JAViewer.getDataSource().link ?: "https://avos.pw"
        return retrofit2.Retrofit.Builder()
            .baseUrl(link)
            .client(okHttpClient)
            .build()
            .create(BasicService::class.java)
    }
}
