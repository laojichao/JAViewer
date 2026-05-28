package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.webkit.CookieManager
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

@AndroidEntryPoint
class WebViewActivity : SecureActivity() {

    companion object {
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

        setSupportActionBar(binding.toolbar)

        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(binding.webView, true)
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.setOnTouchListener { _, _ -> locked }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                if (url.contains("?hash=")) {
                    val cookie = cookieManager.getCookie(url)
                    val request = Request.Builder()
                        .url(url)
                        .header("Referer", "https://javiewer.github.io/")
                        .header("Cookie", cookie ?: "")
                        .get()
                        .build()
                    httpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}
                        override fun onResponse(call: Call, response: Response) {
                            if (isFinishing) return
                            try {
                                val json = response.body()?.string() ?: return
                                val obj = Gson().fromJson(json, JsonObject::class.java)
                                val playBack = obj.get("url").asString
                                testVideoPlayBack(playBack)
                            } catch (_: Exception) {}
                        }
                    })
                }
                return super.shouldInterceptRequest(view, url)
            }
        }

        binding.buttonUnlock.setOnClickListener { onUnlock(it as Button) }

        val safeUrl = TextUtils.htmlEncode(embeddedUrl)
        binding.webView.loadDataWithBaseURL(
            "https://javiewer.github.io/",
            "<iframe width=\"100%\" height=\"100%\" src=\"$safeUrl\" frameborder=\"0\" allowfullscreen></iframe>",
            "text/html", null, null
        )
    }

    private fun testVideoPlayBack(url: String) {
        val request = Request.Builder().url(url).get().build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                if (isFinishing) return
                if (response.code() == 200) {
                    val intent = Intent().apply {
                        putExtra("m3u8", response.request().url().toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
    }

    fun onUnlock(button: Button) {
        locked = false
        button.isEnabled = false
        Toast.makeText(this, "锁定已解除，请完成验证码，不要按任何其他地方！", Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
