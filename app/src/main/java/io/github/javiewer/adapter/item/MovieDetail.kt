package io.github.javiewer.adapter.item

import kotlinx.parcelize.Parcelize

/**
 * 影片详情数据模型，包含从详情页解析出的完整信息。
 *
 * 由 [io.github.javiewer.network.provider.AVMOProvider.parseMoviesDetail] 解析 HTML 生成。
 *
 * @property title 影片标题
 * @property coverUrl 大封面图 URL
 * @property headers 详情头部信息列表（如日期、时长、制作商等）
 * @property screenshots 截图列表
 * @property genres 所属类别列表
 * @property actresses 出演女优列表
 */
data class MovieDetail(
    var title: String = "",
    var coverUrl: String = "",
    val headers: List<Header> = emptyList(),
    val screenshots: List<Screenshot> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val actresses: List<Actress> = emptyList()
) {
    /**
     * 影片详情头部信息条目，表示一个键值对（如 "日期: 2024-01-01"）。
     *
     * 可选地包含关联链接（如制作商链接），通过继承的 [Linkable.link] 字段存储。
     *
     * @property name 信息名称（如 "日期", "时长", "制作商"）
     * @property value 信息值
     */
    @Parcelize
    data class Header(
        var name: String = "",
        var value: String = ""
    ) : Linkable() {
        companion object {
            /**
             * 工厂方法，创建 [Header] 实例。
             *
             * @param name 信息名称
             * @param value 信息值，为 null 时使用空字符串
             * @param link 关联链接，为 null 时无链接
             * @return 新建的 [Header] 实例
             */
            @JvmStatic
            fun create(name: String, value: String?, link: String?): Header {
                return Header(name = name, value = value ?: "").apply { this.link = link }
            }
        }
    }
}
