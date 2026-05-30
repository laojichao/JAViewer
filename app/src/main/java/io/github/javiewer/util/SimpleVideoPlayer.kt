package io.github.javiewer.util

import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import cn.jzvd.JZMediaManager
import cn.jzvd.JZUserAction
import cn.jzvd.JZUserActionStandard
import cn.jzvd.JZUtils
import cn.jzvd.JZVideoPlayerStandard
import io.github.javiewer.R

class SimpleVideoPlayer : JZVideoPlayerStandard {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int = R.layout.layout_video_player

    override fun updateStartImage() {
        when (currentState) {
            CURRENT_STATE_PLAYING -> {
                startButton.visibility = VISIBLE
                startButton.setImageResource(R.drawable.selector_pause)
                replayTextView.visibility = INVISIBLE
            }
            CURRENT_STATE_ERROR -> {
                startButton.visibility = INVISIBLE
                replayTextView.visibility = INVISIBLE
            }
            CURRENT_STATE_AUTO_COMPLETE -> {
                startButton.visibility = VISIBLE
                startButton.setImageResource(R.drawable.selector_replay)
                replayTextView.visibility = VISIBLE
            }
            else -> {
                startButton.setImageResource(R.drawable.selector_play)
                replayTextView.visibility = INVISIBLE
            }
        }
    }

    override fun showVolumeDialog(deltaY: Float, volumePercent0: Int) {
        if (mVolumeDialog == null) {
            val localView = LayoutInflater.from(context).inflate(R.layout.dialog_player_volume, null)
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip)
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume)
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar)
            mVolumeDialog = createDialogWithView(localView)
        }
        if (!mVolumeDialog.isShowing) mVolumeDialog.show()
        var volumePercent = volumePercent0
        if (volumePercent <= 0) mDialogVolumeImageView.setBackgroundResource(R.drawable.ic_player_volume_off)
        else if (-deltaY <= 0) mDialogVolumeImageView.setBackgroundResource(R.drawable.ic_player_volume_down)
        else mDialogVolumeImageView.setBackgroundResource(R.drawable.ic_player_volume_up)
        volumePercent = volumePercent.coerceIn(0, 100)
        mDialogVolumeTextView.text = "$volumePercent%"
        mDialogVolumeProgressBar.progress = volumePercent
        onCLickUiToggleToClear()
    }

    override fun showProgressDialog(deltaX: Float, seekTime: String, seekTimePosition: Long, totalTime: String, totalTimeDuration: Long) {
        if (mProgressDialog == null) {
            val localView = LayoutInflater.from(context).inflate(R.layout.dialog_player_progress, null)
            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar)
            mDialogSeekTime = localView.findViewById(R.id.tv_current)
            mDialogTotalTime = localView.findViewById(R.id.tv_duration)
            mDialogIcon = localView.findViewById(R.id.duration_image_tip)
            mProgressDialog = createDialogWithView(localView)
        }
        if (!mProgressDialog.isShowing) mProgressDialog.show()
        mDialogSeekTime.text = seekTime
        mDialogTotalTime.text = " / $totalTime"
        mDialogProgressBar.progress = if (totalTimeDuration <= 0) 0 else (seekTimePosition * 100 / totalTimeDuration).toInt()
        mDialogIcon.setBackgroundResource(if (deltaX > 0) R.drawable.ic_player_fast_forward else R.drawable.ic_player_fast_rewind)
        onCLickUiToggleToClear()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val id = v.id
        if (id == cn.jzvd.R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    if (mChangePosition) {
                        val duration = duration
                        val progress = (mSeekTimePosition * 100 / (if (duration == 0L) 1 else duration)).toInt()
                        bottomProgressBar.progress = progress
                    }
                    if (!mChangePosition && !mChangeVolume) {
                        onEvent(JZUserActionStandard.ON_CLICK_BLANK)
                        onClickUiToggle()
                    }
                }
            }
        } else if (id == cn.jzvd.R.id.bottom_seek_progress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> cancelDismissControlViewTimer()
                MotionEvent.ACTION_UP -> startDismissControlViewTimer()
            }
        }
        return onTouch0(v, event)
    }

    @Suppress("ClickableViewAccessibility")
    fun onTouch0(v: View, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val id = v.id
        if (id == cn.jzvd.R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i(TAG, "onTouch surfaceContainer actionDown [${hashCode()}] ")
                    mTouchingProgressBar = true
                    mDownX = x
                    mDownY = y
                    mChangeVolume = false
                    mChangePosition = false
                    mChangeBrightness = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = x - mDownX
                    val deltaY = y - mDownY
                    val absDeltaX = Math.abs(deltaX)
                    val absDeltaY = Math.abs(deltaY)
                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                        if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                cancelProgressTimer()
                                if (absDeltaX >= THRESHOLD) {
                                    if (currentState != CURRENT_STATE_ERROR) {
                                        mChangePosition = true
                                        mGestureDownPosition = currentPositionWhenPlaying
                                    }
                                } else {
                                    if (mDownX < mScreenWidth * 0.5f) {
                                        mChangeBrightness = true
                                        val lp = JZUtils.getWindow(context).attributes
                                        mGestureDownBrightness = if (lp.screenBrightness < 0) {
                                            try {
                                                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
                                            } catch (_: Exception) { 0f }
                                        } else {
                                            lp.screenBrightness * 255f
                                        }
                                    } else {
                                        mChangeVolume = true
                                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                                    }
                                }
                            }
                        }
                    }
                    if (mChangePosition) {
                        val totalTimeDuration = duration
                        mSeekTimePosition = (mGestureDownPosition + deltaX * 90000 / mScreenWidth).toLong()
                        if (mSeekTimePosition > totalTimeDuration) mSeekTimePosition = totalTimeDuration
                        showProgressDialog(deltaX, JZUtils.stringForTime(mSeekTimePosition), mSeekTimePosition, JZUtils.stringForTime(totalTimeDuration), totalTimeDuration)
                    }
                    if (mChangeVolume) {
                        val dY = -deltaY
                        val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                        val deltaV = (max * dY * 3 / mScreenHeight).toInt()
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0)
                        val volumePercent = (mGestureDownVolume * 100 / max + dY * 3 * 100 / mScreenHeight).toInt()
                        showVolumeDialog(-dY, volumePercent)
                    }
                    if (mChangeBrightness) {
                        val dY = -deltaY
                        val deltaV = (255 * dY * 3 / mScreenHeight).toInt()
                        val params = JZUtils.getWindow(context).attributes
                        params.screenBrightness = ((mGestureDownBrightness + deltaV) / 255f).coerceIn(0.01f, 1f)
                        JZUtils.getWindow(context).attributes = params
                    }
                }
                MotionEvent.ACTION_UP -> {
                    mTouchingProgressBar = false
                    dismissProgressDialog()
                    dismissVolumeDialog()
                    dismissBrightnessDialog()
                    if (mChangePosition) {
                        onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_POSITION)
                        JZMediaManager.seekTo(mSeekTimePosition)
                        val duration = duration
                        val progress = (mSeekTimePosition * 100 / (if (duration == 0L) 1 else duration)).toInt()
                        progressBar.progress = progress
                    }
                    if (mChangeVolume) onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME)
                    startProgressTimer()
                }
            }
        }
        return false
    }

    companion object {
        const val THRESHOLD = 10
    }
}
