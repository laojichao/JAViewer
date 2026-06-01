package io.github.javiewer.adapter.item

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 下载链接数据模型，表示从种子搜索站点解析出的资源条目。
 *
 * 包含文件标题、大小、日期、详情页链接及磁力链接信息。
 * 磁力链接通过 [magnetLinkData] 私有字段存储，需通过 [hasMagnetLink] 和 [getMagnetLinkStr] 访问。
 *
 * @property title 资源标题
 * @property size 文件大小（如 "1.2GB"）
 * @property date 发布日期
 * @property magnetLinkData 内部存储的磁力链接数据
 * @property link 资源详情页 URL，可能为 null
 */
@Parcelize
data class DownloadLink(
    var title: String = "",
    var size: String = "",
    var date: String = "",
    @SerializedName("magnetLink")
    private var magnetLinkData: MagnetLink = MagnetLink(),
    override var link: String? = null
) : Linkable(link), Parcelable {

    /** 是否已包含磁力链接 */
    fun hasMagnetLink(): Boolean = magnetLinkData.magnetLink != null

    /** 获取磁力链接字符串，未获取时返回 null */
    fun getMagnetLinkStr(): String? = magnetLinkData.magnetLink

    override fun toString(): String =
        "DownloadLink(title=$title, size=$size, date=$date, link=$link, magnetLink=$magnetLinkData)"

    companion object {
        /**
         * 工厂方法，创建 [DownloadLink] 实例。
         *
         * @param title 资源标题
         * @param size 文件大小
         * @param date 发布日期
         * @param link 详情页 URL
         * @param magnetLink 磁力链接字符串
         * @return 新建的 [DownloadLink] 实例
         */
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
