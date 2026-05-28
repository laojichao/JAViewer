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

object ViewUtil {

    @Suppress("DEPRECATION")
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

    @JvmStatic
    fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    @JvmStatic
    fun pxToDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()

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

    @JvmStatic
    fun getStatusBarHeight(activity: Activity): Int {
        val rect = android.graphics.Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return rect.top
    }
}
