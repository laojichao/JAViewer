package io.github.javiewer.view

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.widget.NestedScrollView

/**
 * 视图工具类，提供常用的 UI 辅助方法。
 */
object ViewUtil {

    /**
     * 将图标垂直居中对齐到目标视图。
     * 通过 ViewTreeObserver 监听布局完成后计算并设置顶部 margin。
     *
     * @param icon 要对齐的图标视图
     * @param view 目标对齐视图
     */
    @JvmStatic
    fun alignIconToView(icon: View, view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val viewMargin = view.layoutParams as ViewGroup.MarginLayoutParams
                val iconMargin = icon.layoutParams as ViewGroup.MarginLayoutParams
                var topMargin = viewMargin.topMargin
                topMargin += (view.measuredHeight - icon.measuredHeight) / 2
                iconMargin.topMargin = topMargin
                icon.layoutParams = iconMargin
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /**
     * dp 转 px。
     *
     * @param dp dp 值
     * @return 对应的 px 值
     */
    @JvmStatic
    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    /**
     * px 转 dp。
     *
     * @param px px 值
     * @return 对应的 dp 值
     */
    @JvmStatic
    fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * 对 ImageView 应用顶部裁剪缩放。
     * 图片按宽度适配，顶部对齐，超出部分裁剪。
     *
     * @param view 目标 ImageView
     */
    @JvmStatic
    fun imageTopCrop(view: ImageView) {
        view.scaleType = ImageView.ScaleType.MATRIX
        val matrix = view.imageMatrix
        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        val drawableWidth = view.drawable.intrinsicWidth
        val drawableHeight = view.drawable.intrinsicHeight
        val scale = if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            viewHeight.toFloat() / drawableHeight.toFloat()
        } else {
            viewWidth.toFloat() / drawableWidth.toFloat()
        }
        matrix.setScale(scale, scale)
        view.imageMatrix = matrix
    }

    /**
     * 将 NestedScrollView 的全部内容渲染为 Bitmap。
     * 用于截图分享功能。
     *
     * @param scrollView 目标 ScrollView
     * @return 渲染后的 Bitmap
     */
    @JvmStatic
    fun getBitmapByView(scrollView: NestedScrollView): Bitmap {
        var h = 0
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"))
        }
        val bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }

    /**
     * 获取状态栏高度。
     *
     * @param activity 当前 Activity
     * @return 状态栏高度（px）
     */
    @JvmStatic
    fun getStatusBarHeight(activity: Activity): Int {
        val rect = android.graphics.Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return rect.top
    }
}
