package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.databinding.ActivityWebViewBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * WebView Activity，用于加载嵌入式视频 iframe 并提取 m3u8 播放地址。
 *
 * 通过拦截包含 `?hash=` 的网络请求，解析 JSON 响应获取视频 URL，
 * 然后测试播放地址可用性后返回给调用方。
 *
 * 包含"解锁"按钮用于允许 WebView 触摸事件（验证码场景）。
 */
@AndroidEntryPoint
class WebViewActivity : SecureActivity() {

    companion object {
        /** 允许加载嵌入式视频的可信域名白名单 */
        private val ALLOWED_EMBED_DOMAINS = setOf(
            "javiewer.github.io",
            "openload.co",
            "oload.stream",
            "streamango.com",
            "verystream.com",
            "waaw.tv",
            "hqq.tv",
            "netu.tv",
            "playtube.ws",
            "fcdn.stream",
            "mystream.to",
            "femax20.com",
            "embed.media",
            "videocloud.co",
            "cdnvideo.me",
        )

        private fun isAllowedEmbedUrl(url: String): Boolean {
            return try {
                val host = Uri.parse(url).host?.lowercase() ?: return false
                ALLOWED_EMBED_DOMAINS.any { allowed ->
                    host == allowed || host.endsWith(".$allowed")
                }
            } catch (_: Exception) {
                false
            }
        }

        private val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Connection", "keep-alive")
                    .header("Accept", "*/*")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .build()
                chain.proceed(request)
            })
            .build()

        /**
         * 创建启动 Intent。
         *
         * @param context 上下文
         * @param embeddedUrl 嵌入式视频 iframe URL
         * @return 配置好的 Intent
         */
        @JvmStatic
        fun newIntent(context: Context, embeddedUrl: String): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                putExtra("embedded_url", embeddedUrl)
            }
        }
    }

    private lateinit var binding: ActivityWebViewBinding
    private var locked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val embeddedUrl = intent.getStringExtra("embedded_url") ?: ""

        if (embeddedUrl.isEmpty() || !isAllowedEmbedUrl(embeddedUrl)) {
            Toast.makeText(this, "不允许加载该链接", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.setOnTouchListener { _, _ -> locked }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                val url = request.url.toString()
                if (url.contains("?hash=")) {
                    val cookie = cookieManager.getCookie(url)
                    val hashRequest = Request.Builder()
                        .url(url)
                        .header("Referer", "https://javiewer.github.io/")
                        .header("Cookie", cookie ?: "")
                        .get()
                        .build()
                    httpClient.newCall(hashRequest).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }
                        override fun onResponse(call: Call, response: Response) {
                            if (isFinishing) {
                                response.close()
                                return
                            }
                            try {
                                val json = response.body?.string() ?: return
                                val obj = Gson().fromJson(json, JsonObject::class.java)
                                val playBack = obj.get("url").asString
                                testVideoPlayBack(playBack)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                response.close()
                            }
                        }
                    })
                }
                return super.shouldInterceptRequest(view, request)
            }
        }

        binding.buttonUnlock.setOnClickListener { onUnlock(binding.buttonUnlock) }

        val safeUrl = TextUtils.htmlEncode(embeddedUrl)
        binding.webView.loadDataWithBaseURL(
            "https://javiewer.github.io/",
            "<iframe width=\"100%\" height=\"100%\" src=\"$safeUrl\" frameborder=\"0\" allowfullscreen></iframe>",
            "text/html", null, null
        )
    }

    /** 测试视频播放地址可用性，成功后返回 m3u8 URL */
    private fun testVideoPlayBack(url: String) {
        val request = Request.Builder().url(url).get().build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (isFinishing) {
                    response.close()
                    return
                }
                if (response.isSuccessful) {
                    val m3u8Url = response.request.url.toString()
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            val intent = Intent().apply {
                                putExtra("m3u8", m3u8Url)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }
                response.close()
            }
        })
    }

    /** 解除 WebView 触摸锁定，允许用户完成验证码 */
    fun onUnlock(button: Button) {
        locked = false
        button.isEnabled = false
        Toast.makeText(this, "锁定已解除，请完成验证码，不要按任何其他地方！", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        binding.webView.stopLoading()
        binding.webView.destroy()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
