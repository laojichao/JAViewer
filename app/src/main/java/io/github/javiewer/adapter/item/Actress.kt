package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
        @JvmStatic
        fun create(name: String, imageUrl: String, detailUrl: String): Actress {
            return Actress(name = name, imageUrl = imageUrl, link = detailUrl)
        }
    }
}
