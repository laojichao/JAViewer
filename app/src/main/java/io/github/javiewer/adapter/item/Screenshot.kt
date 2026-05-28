package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Screenshot(
    var thumbnailUrl: String = "",
    override var link: String? = null
) : Linkable(link), Parcelable {

    val imageUrl: String? get() = link

    companion object {
        @JvmStatic
        fun create(thumbnailUrl: String, imageUrl: String): Screenshot {
            return Screenshot(thumbnailUrl = thumbnailUrl, link = imageUrl)
        }
    }
}
