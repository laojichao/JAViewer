package io.github.javiewer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.network.BasicService
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.security.MessageDigest

/**
 * 应用入口类，使用 Hilt 依赖注入框架。
 *
 * 包含全局静态状态和工具方法。部分静态字段（[SERVICE]、[CONFIGURATIONS]、[DATA_SOURCES]）
 * 正在被 Hilt 依赖注入逐步替代，目前仍作为旧代码的兼容层保留。
 *
 * **重要**：新代码应通过 Hilt 注入依赖，而非直接访问此伴生对象的静态字段。
 */
@HiltAndroidApp
class JAViewer : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        private const val TAG = "JAViewer"

        private val webkit = "${(System.currentTimeMillis() % 550)}.${(System.currentTimeMillis() % 99)}"
        private val chrome = "${(System.currentTimeMillis() % 4000)}.${(System.currentTimeMillis() % 999)}"

        /** 动态生成的 User-Agent 字符串，模拟 Chrome 浏览器 */
        @JvmField
        val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/$webkit (KHTML, like Gecko) Chrome/89.0.$chrome Safari/$webkit"

        /** 可用数据源列表，由 [io.github.javiewer.repository.DataSourceRepository] 管理 */
        @JvmField
        val DATA_SOURCES: MutableList<DataSource> = mutableListOf()

        /** 旧版用户配置对象，由 Hilt [io.github.javiewer.di.AppModule] 提供 */
        @JvmField
        @Volatile
        var CONFIGURATIONS: Configurations? = null

        /** 当前 BasicService 实例，基于当前数据源 URL 创建 */
        @JvmField
        @Volatile
        var SERVICE: BasicService? = null

        /** 域名重写映射表（旧域名 -> 当前域名） */
        @JvmField
        val hostReplacements: MutableMap<String, String> = java.util.concurrent.ConcurrentHashMap()

        /** 共享 OkHttpClient 实例，配置了域名重写拦截器和 Cookie 管理 */
        @JvmField
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .url(replaceUrl(original.url))
                    .header("User-Agent", USER_AGENT)
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

        /**
         * 获取当前数据源，未设置时默认为 [DataSource.AVMO]。
         *
         * @return 当前数据源
         */
        @JvmStatic
        fun getDataSource(): DataSource = CONFIGURATIONS?.getDataSource() ?: DataSource.AVMO

        /**
         * 重建 [BasicService] 实例，基于当前数据源的 URL。
         * 数据源切换后需调用此方法。
         */
        @JvmStatic
        fun recreateService() {
            val link = getDataSource().link ?: return
            Log.d(TAG, "recreateService: $link")
            SERVICE = retrofit2.Retrofit.Builder()
                .baseUrl(link)
                .client(httpClient)
                .build()
                .create(BasicService::class.java)
        }

        /**
         * 获取应用外部存储目录。
         * Android Q+ 使用应用专属目录，旧版本使用公共外部存储。
         *
         * @param context 上下文
         * @return 存储目录 File 对象
         */
        @JvmStatic
        fun getStorageDir(context: Context): File {
            val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(context.getExternalFilesDir(null), "JAViewer/")
            } else {
                @Suppress("DEPRECATION")
                File(Environment.getExternalStorageDirectory(), "JAViewer/")
            }
            dir.mkdirs()
            return dir
        }

        /**
         * 域名重写：如果 URL 的主机名在 [hostReplacements] 映射中，
         * 则替换为目标主机名。
         *
         * @param url 原始 URL
         * @return 重写后的 URL
         */
        @JvmStatic
        fun replaceUrl(url: HttpUrl): HttpUrl {
            val host = url.host
            val replacement = hostReplacements[host] ?: return url
            return url.newBuilder().host(replacement).build()
        }

        /** kotlinx.serialization JSON 实例 */
        val kotlinJson = Json { ignoreUnknownKeys = true }

        /**
         * 使用 kotlinx.serialization 反序列化 JSON 字符串。
         *
         * @param json JSON 字符串
         * @return 反序列化后的对象
         */
        @JvmStatic
        inline fun <reified T> parseJson(json: String): T {
            return kotlinJson.decodeFromString(json)
        }

        /**
         * 将字节数组转换为十六进制字符串。
         *
         * @param bytes 字节数组
         * @return 十六进制字符串
         */
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

        /**
         * 打开支付宝捐赠二维码页面。
         *
         * @param context 上下文
         */
        @JvmStatic
        fun a(context: Context) {
            val url = "https://qr.alipay.com/a6x05027ymf6n8kl0qkoa54"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        /**
         * 生成 MD5 签名，用于视频播放 URL 的 sign 参数。
         *
         * @param s1 第一个字符串（通常是 vid）
         * @param s2 第二个字符串（通常是时间戳）
         * @return MD5 十六进制字符串，失败时返回 null
         */
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
