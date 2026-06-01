package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 所有可导航数据项的基类，包含一个可空的链接字段。
 *
 * 提供基于 [link] 的 equals/hashCode 实现，子类可覆盖以使用更精确的标识。
 *
 * @property link 该项关联的 URL 链接，可能为 null
 */
@Parcelize
open class Linkable(
    open var link: String? = null
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Linkable) return false
        return link == other.link
    }

    override fun hashCode(): Int = link?.hashCode() ?: 0

    override fun toString(): String = "Linkable(link=$link)"
}
