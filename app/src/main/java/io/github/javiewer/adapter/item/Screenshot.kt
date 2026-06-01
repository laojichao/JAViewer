package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 影片截图数据模型，包含缩略图和原图 URL。
 *
 * @property thumbnailUrl 缩略图 URL，用于列表展示
 * @property link 原图 URL，通过 [imageUrl] 属性访问
 */
@Parcelize
data class Screenshot(
    var thumbnailUrl: String = "",
    override var link: String? = null
) : Linkable(link), Parcelable {

    /** 原图 URL，与 [link] 相同 */
    val imageUrl: String? get() = link

    companion object {
        /**
         * 工厂方法，创建 [Screenshot] 实例。
         *
         * @param thumbnailUrl 缩略图 URL
         * @param imageUrl 原图 URL
         * @return 新建的 [Screenshot] 实例
         */
        @JvmStatic
        fun create(thumbnailUrl: String, imageUrl: String): Screenshot {
            return Screenshot(thumbnailUrl = thumbnailUrl, link = imageUrl)
        }
    }
}
