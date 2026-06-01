package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 影片类别数据模型，表示影片分类标签（如 "有碼", "無碼" 等）。
 *
 * @property name 类别名称
 * @property link 该类别对应的影片列表页 URL
 */
@Parcelize
data class Genre(
    var name: String = "",
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun toString(): String = "$name:$link"

    companion object {
        /**
         * 工厂方法，创建 [Genre] 实例。
         *
         * @param name 类别名称
         * @param link 类别列表页 URL
         * @return 新建的 [Genre] 实例
         */
        @JvmStatic
        fun create(name: String, link: String): Genre {
            return Genre(name = name, link = link)
        }
    }
}
