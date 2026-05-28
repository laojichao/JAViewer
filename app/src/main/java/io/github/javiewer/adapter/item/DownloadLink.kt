package io.github.javiewer.adapter.item

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadLink(
    var title: String = "",
    var size: String = "",
    var date: String = "",
    @SerializedName("magnetLink")
    private var magnetLinkData: MagnetLink = MagnetLink(),
    override var link: String? = null
) : Linkable(link), Parcelable {

    fun hasMagnetLink(): Boolean = magnetLinkData.magnetLink != null

    fun getMagnetLinkStr(): String? = magnetLinkData.magnetLink

    override fun toString(): String =
        "DownloadLink(title=$title, size=$size, date=$date, link=$link, magnetLink=$magnetLinkData)"

    companion object {
        @JvmStatic
        fun create(title: String, size: String, date: String, link: String?, magnetLink: String?): DownloadLink {
            return DownloadLink(
                title = title,
                size = size,
                date = date,
                magnetLinkData = MagnetLink.create(magnetLink),
                link = link
            )
        }
    }
}
