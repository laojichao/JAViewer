package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    var name: String = "",
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun toString(): String = "$name:$link"

    companion object {
        @JvmStatic
        fun create(name: String, link: String): Genre {
            return Genre(name = name, link = link)
        }
    }
}
