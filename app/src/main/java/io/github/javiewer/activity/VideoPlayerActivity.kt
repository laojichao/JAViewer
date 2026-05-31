package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import io.github.javiewer.R
import io.github.javiewer.util.Media3PlayerImpl

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerImpl: Media3PlayerImpl
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen immersive
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )

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
        @Suppress("DEPRECATION")
        playerView.onPause()
    }

    override fun onResume() {
        super.onResume()
        @Suppress("DEPRECATION")
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

        fun start(context: Context, videoUrl: String, title: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }
}
