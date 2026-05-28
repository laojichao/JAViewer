package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    var title: String = "",
    var code: String = "",
    var coverUrl: String = "",
    var date: String = "",
    var hot: Boolean = false,
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movie) return false
        return code == other.code
    }

    override fun hashCode(): Int = code.hashCode()

    companion object {
        @JvmStatic
        fun create(title: String, code: String, date: String, coverUrl: String, detailUrl: String, hot: Boolean): Movie {
            return Movie(title = title, code = code, date = date, coverUrl = coverUrl, hot = hot, link = detailUrl)
        }
    }
}
