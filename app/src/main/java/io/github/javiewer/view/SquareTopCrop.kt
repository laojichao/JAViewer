package io.github.javiewer.view

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * Glide 图片变换：从顶部居中裁剪为正方形。
 *
 * 用于女优头像显示，确保图片为正方形且保留顶部内容（人脸通常在上方）。
 */
class SquareTopCrop : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val size = minOf(toTransform.width, toTransform.height)
        return Bitmap.createBitmap(toTransform, (toTransform.width - size) / 2, 0, size, size)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray())
    }

    companion object {
        private const val ID = "io.github.javiewer.view.SquareTopCrop"
    }
}
