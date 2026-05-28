package io.github.javiewer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader
import dagger.hilt.android.HiltAndroidApp
import io.github.javiewer.adapter.item.DataSource
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.security.MessageDigest

@HiltAndroidApp
class JAViewer : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        private const val TAG = "JAViewer"

        private val webkit = "${(System.currentTimeMillis() % 550)}.${(System.currentTimeMillis() % 99)}"
        private val chrome = "${(System.currentTimeMillis() % 4000)}.${(System.currentTimeMillis() % 999)}"
        @JvmField
        val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/$webkit (KHTML, like Gecko) Chrome/89.0.$chrome Safari/$webkit"

        @JvmField
        val DATA_SOURCES: MutableList<DataSource> = mutableListOf()

        @JvmField
        var CONFIGURATIONS: Configurations? = null

        @JvmField
        var SERVICE: BasicService? = null

        @JvmField
        val hostReplacements: MutableMap<String, String> = mutableMapOf()

        private val GSON: Gson = GsonBuilder().create()

        @JvmField
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .url(replaceUrl(original.url()))
                    .header("User-Agent", USER_AGENT)
                    .build()
                chain.proceed(request)
            })
            .cookieJar(object : CookieJar {
                private val cookieStore = mutableMapOf<HttpUrl, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url] ?: emptyList()
                }
            })
            .build()

        @JvmStatic
        fun getDataSource(): DataSource = CONFIGURATIONS!!.getDataSource()

        @JvmStatic
        fun recreateService() {
            Log.d(TAG, "recreateService: ${getDataSource().link}")
            SERVICE = retrofit2.Retrofit.Builder()
                .baseUrl(getDataSource().link!!)
                .client(httpClient)
                .build()
                .create(BasicService::class.java)
        }

        @JvmStatic
        fun getStorageDir(): File {
            val dir = File(Environment.getExternalStorageDirectory(), "JAViewer/")
            dir.mkdirs()
            return dir
        }

        @JvmStatic
        fun replaceUrl(url: HttpUrl): HttpUrl {
            val host = url.host
            val replacement = hostReplacements[host] ?: return url
            return url.newBuilder().host(replacement).build()
        }

        @JvmStatic
        fun <T> parseJson(beanClass: Class<T>, reader: JsonReader): T {
            return GSON.fromJson(reader, beanClass)
        }

        @JvmStatic
        fun <T> parseJson(beanClass: Class<T>, json: String): T {
            return GSON.fromJson(json, beanClass)
        }

        @JvmStatic
        fun bytesToHex(bytes: ByteArray): String {
            val hexArray = "0123456789abcdef".toCharArray()
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }

        @JvmStatic
        fun a(context: Context) {
            val url = "https://qr.alipay.com/a6x05027ymf6n8kl0qkoa54"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        @JvmStatic
        fun b(s1: String, s2: String): String? {
            return try {
                val md = MessageDigest.getInstance("MD5")
                val bytes = md.digest("$s1${s2}Brynhildr".toByteArray())
                bytesToHex(bytes)
            } catch (_: Exception) {
                null
            }
        }
    }
}
