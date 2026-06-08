package io.github.javiewer.util

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import io.github.javiewer.JAViewer

/**
 * Media3 ExoPlayer 封装，替代旧版 JiaoZi Video Player。
 *
 * 支持 HLS（`.m3u8`）和渐进式（MP4）两种媒体源格式，
 * 自动根据 URL 特征选择合适的 MediaSource。
 *
 * 使用 [getPlayer] 获取 ExoPlayer 实例，通过 [setMediaUrl] 设置播放地址。
 * 生命周期结束时需调用 [release] 释放资源。
 *
 * @property context 应用上下文
 */
@OptIn(UnstableApi::class)
class Media3PlayerImpl(private val context: Context) {

    private var player: ExoPlayer? = null

    /**
     * 获取 ExoPlayer 实例，首次调用时自动创建。
     *
     * @return ExoPlayer 实例
     */
    fun getPlayer(): ExoPlayer {
        return player ?: createPlayer().also { player = it }
    }

    /** 创建 ExoPlayer 实例 */
    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
            }
    }

    /**
     * 设置播放地址并准备播放。
     *
     * 根据 URL 自动选择 HLS 或渐进式 MediaSource。
     *
     * @param url 视频播放地址（HLS 或 MP4）
     */
    fun setMediaUrl(url: String) {
        val player = getPlayer()
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val source = createMediaSource(url, mediaItem)
        player.setMediaSource(source)
        player.prepare()
    }

    /** 根据 URL 特征创建对应的 MediaSource */
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

    /** 释放播放器资源 */
    fun release() {
        player?.release()
        player = null
    }

    /** 检查播放器是否正在播放 */
    fun isPlaying(): Boolean = player?.isPlaying ?: false

    /** 暂停播放 */
    fun pause() {
        player?.playWhenReady = false
    }

    /** 恢复播放 */
    fun resume() {
        player?.playWhenReady = true
    }
}
