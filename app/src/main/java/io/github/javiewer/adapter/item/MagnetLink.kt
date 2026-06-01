package io.github.javiewer.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 磁力链接数据模型，封装 magnet URI。
 *
 * 通过 [create] 工厂方法创建时会自动截断 `&` 之后的 tracker 参数，
 * 仅保留核心的 info-hash 部分。
 *
 * @property magnetLink 处理后的磁力链接字符串，可能为 null
 */
@Parcelize
data class MagnetLink(
    var magnetLink: String? = null
) : Parcelable {

    override fun toString(): String = "MagnetLink(magnetLink=$magnetLink)"

    companion object {
        /**
         * 创建 [MagnetLink] 实例，自动去除 `&` 之后的 tracker 参数。
         *
         * @param magnetLink 原始磁力链接字符串
         * @return 处理后的 [MagnetLink] 实例
         */
        @JvmStatic
        fun create(magnetLink: String?): MagnetLink {
            val processed = magnetLink?.let {
                if (it.contains("&")) it.substring(0, it.indexOf("&")) else it
            }
            return MagnetLink(magnetLink = processed)
        }
    }
}
