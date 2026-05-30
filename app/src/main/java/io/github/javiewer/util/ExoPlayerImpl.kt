package io.github.javiewer.util

import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import cn.jzvd.JZMediaInterface
import cn.jzvd.JZMediaManager
import cn.jzvd.JZVideoPlayerManager
import io.github.javiewer.R

@Suppress("DEPRECATION")
class ExoPlayerImpl : JZMediaInterface(), Player.Listener {

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var mainHandler: Handler? = null
    private var callback: Runnable? = null
    private val tag = "JZExoPlayer"
    private var previousSeek = 0L

    override fun start() {
        simpleExoPlayer?.playWhenReady = true
    }

    override fun prepare() {
        Log.e(tag, "prepare")
        mainHandler = Handler()
        val context = JZVideoPlayerManager.getCurrentJzvd().context

        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(360000, 600000, 1000, 5000)
            .setPrioritizeTimeOverSizeThresholds(false)
            .build()
        val renderersFactory = DefaultRenderersFactory(context)
        simpleExoPlayer = SimpleExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()

        val dataSourceFactory = DefaultDataSourceFactory(
            context, null,
            DefaultHttpDataSource.Factory()
                .setUserAgent(
                    com.google.android.exoplayer2.util.Util.getUserAgent(
                        context, context.resources.getString(R.string.app_name)
                    )
                )
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setAllowCrossProtocolRedirects(true)
        )

        val currUrl = currentDataSource.toString()
        Log.i("CURR URL", currUrl)
        val videoSource = if (currUrl.contains(".m3u8") || currUrl.contains("api.rekonquer.com/psvs")) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(currUrl))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(currUrl))
        }

        simpleExoPlayer?.addListener(this)
        simpleExoPlayer?.prepare(videoSource)
        simpleExoPlayer?.playWhenReady = true
        callback = onBufferingUpdate()
    }

    override fun pause() {
        simpleExoPlayer?.playWhenReady = false
    }

    override fun isPlaying(): Boolean = simpleExoPlayer?.playWhenReady ?: false

    override fun seekTo(time: Long) {
        if (time != previousSeek) {
            simpleExoPlayer?.seekTo(time)
            previousSeek = time
            JZVideoPlayerManager.getCurrentJzvd().seekToInAdvance = time
        }
    }

    override fun release() {
        simpleExoPlayer?.release()
        callback?.let { mainHandler?.removeCallbacks(it) }
    }

    override fun getCurrentPosition(): Long = simpleExoPlayer?.currentPosition ?: 0
    override fun getDuration(): Long = simpleExoPlayer?.duration ?: 0

    override fun setSurface(surface: Surface?) {
        simpleExoPlayer?.setVideoSurface(surface)
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        simpleExoPlayer?.volume = leftVolume
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        JZMediaManager.instance().mainThreadHandler.post {
            val jzvd = JZVideoPlayerManager.getCurrentJzvd() ?: return@post
            when (playbackState) {
                Player.STATE_BUFFERING -> callback?.let { mainHandler?.post(it) }
                Player.STATE_READY -> if (playWhenReady) jzvd.onPrepared()
                Player.STATE_ENDED -> jzvd.onAutoCompletion()
            }
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
        JZMediaManager.instance().mainThreadHandler.post {
            JZVideoPlayerManager.getCurrentJzvd()?.onError(1000, 1000)
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

    override fun onSeekProcessed() {
        JZMediaManager.instance().mainThreadHandler.post {
            JZVideoPlayerManager.getCurrentJzvd()?.onSeekComplete()
        }
    }

    private inner class onBufferingUpdate : Runnable {
        override fun run() {
            val percent = simpleExoPlayer?.bufferedPercentage ?: 0
            JZMediaManager.instance().mainThreadHandler.post {
                JZVideoPlayerManager.getCurrentJzvd()?.setBufferProgress(percent)
            }
            if (percent < 100) {
                mainHandler?.postDelayed(this, 300)
            } else {
                mainHandler?.removeCallbacks(this)
            }
        }
    }
}
