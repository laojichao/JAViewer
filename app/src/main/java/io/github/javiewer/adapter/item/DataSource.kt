package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class DataSource(
    var name: String = "",
    var legacies: List<String>? = null,
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun toString(): String = name

    companion object {
        @JvmField
        val AVMO = DataSource("AVMOO 日本", link = "https://avos.pw")
        @JvmField
        val AVSO = DataSource("AVSOX 日本无码", link = "https://avso.club")
        @JvmField
        val AVXO = DataSource("AVMEMO 欧美", link = "https://avxo.pw")
    }
}
