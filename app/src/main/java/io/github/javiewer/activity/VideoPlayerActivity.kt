package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import io.github.javiewer.R
import io.github.javiewer.util.Media3PlayerImpl

/**
 * 视频播放 Activity，使用 Media3 ExoPlayer 全屏播放视频。
 *
 * 替代旧版 JiaoZi Video Player，支持 HLS 和 MP4 格式。
 * 强制横屏、沉浸式模式、播放结束后自动关闭。
 *
 * 通过 [start] 工厂方法启动。
 */
class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerImpl: Media3PlayerImpl
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen immersive
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: run { finish(); return }
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        playerView = findViewById(R.id.player_view)
        val titleText = findViewById<TextView>(R.id.text_title)
        val backButton = findViewById<ImageButton>(R.id.button_back)

        titleText.text = title
        backButton.setOnClickListener { finish() }

        playerImpl = Media3PlayerImpl(this)
        playerView.player = playerImpl.getPlayer()

        playerImpl.getPlayer().addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    finish()
                }
            }
        })

        playerImpl.setMediaUrl(videoUrl)
    }

    override fun onPause() {
        super.onPause()
        playerImpl.pause()
        playerView.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
        playerImpl.resume()
    }

    override fun onDestroy() {
        playerImpl.release()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_VIDEO_URL = "video_url"
        const val EXTRA_TITLE = "title"

        /**
         * 启动视频播放 Activity。
         *
         * @param context 上下文
         * @param videoUrl 视频播放地址（HLS 或 MP4）
         * @param title 视频标题，显示在左上角
         */
        fun start(context: Context, videoUrl: String, title: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }
}
