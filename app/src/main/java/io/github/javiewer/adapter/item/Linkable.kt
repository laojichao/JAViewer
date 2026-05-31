package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
