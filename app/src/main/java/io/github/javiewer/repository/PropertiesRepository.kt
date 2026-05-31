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

@Singleton
class PropertiesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val httpClient: OkHttpClient
) {
    suspend fun fetchProperties(): Properties? {
        return withContext(Dispatchers.IO) {
            fetchRemoteProperties() ?: fetchLocalProperties()
        }
    }

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

    private fun fetchLocalProperties(): Properties? {
        return try {
            val json = context.assets.open("properties.json").bufferedReader().use { it.readText() }
            com.google.gson.Gson().fromJson(json, Properties::class.java)
        } catch (_: Exception) {
            null
        }
    }
}
