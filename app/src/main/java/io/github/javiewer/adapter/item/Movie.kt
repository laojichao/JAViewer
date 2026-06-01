package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 影片数据模型，表示从 AVMOO 等站点解析出的影片信息。
 *
 * 基于 [code]（影片编号）实现 equals/hashCode，确保同一影片的唯一性。
 *
 * @property title 影片标题
 * @property code 影片编号（如 "ABP-123"），用作唯一标识
 * @property coverUrl 封面图片 URL
 * @property title 发布日期字符串
 * @property hot 是否为热门影片
 * @property link 影片详情页 URL
 */
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
        /**
         * 工厂方法，创建 [Movie] 实例。
         *
         * @param title 影片标题
         * @param code 影片编号
         * @param date 发布日期
         * @param coverUrl 封面图片 URL
         * @param detailUrl 详情页 URL
         * @param hot 是否热门
         * @return 新建的 [Movie] 实例
         */
        @JvmStatic
        fun create(title: String, code: String, date: String, coverUrl: String, detailUrl: String, hot: Boolean): Movie {
            return Movie(title = title, code = code, date = date, coverUrl = coverUrl, hot = hot, link = detailUrl)
        }
    }
}
