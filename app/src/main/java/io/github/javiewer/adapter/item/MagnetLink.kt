package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MagnetLink(
    var magnetLink: String? = null
) : Parcelable {

    override fun toString(): String = "MagnetLink(magnetLink=$magnetLink)"

    companion object {
        @JvmStatic
        fun create(magnetLink: String?): MagnetLink {
            val processed = magnetLink?.let {
                if (it.contains("&")) it.substring(0, it.indexOf("&")) else it
            }
            return MagnetLink(magnetLink = processed)
        }
    }
}
