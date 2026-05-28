package io.github.javiewer.util

import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener
import cn.jzvd.JZMediaInterface
import cn.jzvd.JZMediaManager
import cn.jzvd.JZVideoPlayerManager
import io.github.javiewer.R

@Suppress("DEPRECATION")
class ExoPlayerImpl : JZMediaInterface(), Player.EventListener, VideoListener {

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

        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl(
            DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
            360000, 600000, 1000, 5000, C.LENGTH_UNSET, false
        )
        val renderersFactory = DefaultRenderersFactory(context)
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl)

        val dataSourceFactory = DefaultDataSourceFactory(
            context, null,
            DefaultHttpDataSourceFactory(
                com.google.android.exoplayer2.util.Util.getUserAgent(context, context.resources.getString(R.string.app_name)),
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
            )
        )

        val currUrl = currentDataSource.toString()
        Log.i("CURR URL", currUrl)
        val videoSource = if (currUrl.contains(".m3u8") || currUrl.contains("api.rekonquer.com/psvs")) {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(currUrl), mainHandler, null)
        } else {
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(currUrl))
        }

        simpleExoPlayer?.addVideoListener(this)
        simpleExoPlayer?.addListener(this)
        simpleExoPlayer?.prepare(videoSource)
        simpleExoPlayer?.playWhenReady = true
        callback = onBufferingUpdate()
    }

    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        JZMediaManager.instance().currentVideoWidth = width
        JZMediaManager.instance().currentVideoHeight = height
        JZMediaManager.instance().mainThreadHandler.post {
            JZVideoPlayerManager.getCurrentJzvd()?.onVideoSizeChanged()
        }
    }

    override fun onRenderedFirstFrame() {
        Log.e(tag, "onRenderedFirstFrame")
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

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

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

    override fun onPlayerError(error: ExoPlaybackException?) {
        JZMediaManager.instance().mainThreadHandler.post {
            JZVideoPlayerManager.getCurrentJzvd()?.onError(1000, 1000)
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

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
