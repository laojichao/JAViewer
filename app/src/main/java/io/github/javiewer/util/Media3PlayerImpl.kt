package io.github.javiewer.util

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import io.github.javiewer.JAViewer

@OptIn(UnstableApi::class)
class Media3PlayerImpl(private val context: Context) {

    private var player: ExoPlayer? = null

    fun getPlayer(): ExoPlayer {
        return player ?: createPlayer().also { player = it }
    }

    private fun createPlayer(): ExoPlayer {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(JAViewer.USER_AGENT)
            .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
            .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
            .setAllowCrossProtocolRedirects(true)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        return ExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
            }
    }

    fun setMediaUrl(url: String) {
        val player = getPlayer()
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val source = createMediaSource(url, mediaItem)
        player.setMediaSource(source)
        player.prepare()
    }

    private fun createMediaSource(url: String, mediaItem: MediaItem): MediaSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(JAViewer.USER_AGENT)
            .setAllowCrossProtocolRedirects(true)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        return if (url.contains(".m3u8") || url.contains("api.rekonquer.com/psvs")) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }
    }

    fun release() {
        player?.release()
        player = null
    }

    fun isPlaying(): Boolean = player?.isPlaying ?: false

    fun pause() {
        player?.playWhenReady = false
    }

    fun resume() {
        player?.playWhenReady = true
    }
}
