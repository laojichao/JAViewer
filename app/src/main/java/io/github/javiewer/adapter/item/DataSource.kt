package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 数据源配置模型，定义一个可用的 AVMOO 站点及其历史域名。
 *
 * 应用支持在多个数据源之间切换，每个数据源对应不同的站点（日本有码、日本无码、欧美）。
 * [legacies] 字段用于 URL 重写拦截器，将旧域名请求自动转发到当前域名。
 *
 * @property name 数据源显示名称（如 "AVMOO 日本"）
 * @property legacies 历史域名列表，用于 OkHttp 拦截器的 URL 重写
 * @property link 数据源的当前基础 URL
 */
@Parcelize
data class DataSource(
    var name: String = "",
    var legacies: List<String>? = null,
    override var link: String? = null
) : Linkable(link), Parcelable {

    override fun toString(): String = name

    companion object {
        /** 日本有码数据源 */
        @JvmField
        val AVMO = DataSource("AVMOO 日本", link = "https://avos.pw")

        /** 日本无码数据源 */
        @JvmField
        val AVSO = DataSource("AVSOX 日本无码", link = "https://avso.club")

        /** 欧美数据源 */
        @JvmField
        val AVXO = DataSource("AVMEMO 欧美", link = "https://avxo.pw")
    }
}
