package io.github.javiewer.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javiewer.JAViewer
import io.github.javiewer.Properties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 远程配置仓库，负责从 GitHub 获取 `properties.json` 配置文件。
 *
 * 加载策略：先尝试从 GitHub 远程获取最新配置，失败时回退到本地 assets 中的备份。
 * 配置内容包括最新版本号、更新日志和可用数据源列表。
 *
 * @property context 应用上下文，用于访问 assets 资源
 * @property httpClient OkHttp 客户端，用于发起网络请求
 */
@Singleton
class PropertiesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val httpClient: OkHttpClient
) {
    /**
     * 获取远程配置，远程失败时回退到本地配置。
     *
     * @return 解析后的 [Properties] 实例，获取失败时返回 null
     */
    suspend fun fetchProperties(): Properties? {
        return withContext(Dispatchers.IO) {
            fetchRemoteProperties() ?: fetchLocalProperties()
        }
    }

    /** 从 GitHub 远程获取配置 */
    private fun fetchRemoteProperties(): Properties? {
        return try {
            val url = "https://raw.githubusercontent.com/ipcjs/JAViewer/master/app/src/main/assets/properties.json"
            val request = Request.Builder()
                .url("$url?t=${System.currentTimeMillis() / 1000}")
                .build()
            val response = httpClient.newCall(request).execute()
            response.use {
                if (it.isSuccessful) {
                    val body = it.body?.string() ?: return null
                    com.google.gson.Gson().fromJson(body, Properties::class.java)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    /** 从本地 assets 获取备份配置 */
    private fun fetchLocalProperties(): Properties? {
        return try {
            val json = context.assets.open("properties.json").bufferedReader().use { it.readText() }
            com.google.gson.Gson().fromJson(json, Properties::class.java)
        } catch (_: Exception) {
            null
        }
    }
}
