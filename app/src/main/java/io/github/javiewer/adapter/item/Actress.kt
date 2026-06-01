package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 女优数据模型，表示从站点解析出的女优信息。
 *
 * 基于 [name] 实现 equals/hashCode，确保同一女优的唯一性。
 *
 * @property name 女优名称，用作唯一标识
 * @property imageUrl 头像图片 URL
 * @property link 女优详情页 URL
 */
@Parcelize
data class Actress(
    var name: String = "",
    var imageUrl: String = "",
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Actress) return false
        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    companion object {
        /**
         * 工厂方法，创建 [Actress] 实例。
         *
         * @param name 女优名称
         * @param imageUrl 头像图片 URL
         * @param detailUrl 详情页 URL
         * @return 新建的 [Actress] 实例
         */
        @JvmStatic
        fun create(name: String, imageUrl: String, detailUrl: String): Actress {
            return Actress(name = name, imageUrl = imageUrl, link = detailUrl)
        }
    }
}
