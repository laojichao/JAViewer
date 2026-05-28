package io.github.javiewer.adapter.item

import android.os.Parcel
import android.os.Parcelable

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

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(link)
    }

    companion object CREATOR : Parcelable.Creator<Linkable> {
        override fun createFromParcel(parcel: Parcel): Linkable {
            return Linkable(parcel.readString())
        }

        override fun newArray(size: Int): Array<Linkable?> {
            return arrayOfNulls(size)
        }
    }
}
