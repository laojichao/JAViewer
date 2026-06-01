package io.github.javiewer.network.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Avgle/PSVS 视频搜索 API 的响应数据模型。
 *
 * 两个 API 共用此数据结构，使用 kotlinx.serialization 进行 JSON 反序列化。
 *
 * @property success 请求是否成功
 * @property response 搜索结果响应体
 */
@Serializable
data class AvgleSearchResult(
    val success: Boolean = false,
    val response: Response = Response()
) {
    /**
     * 搜索结果响应体。
     *
     * @property has_more 是否还有更多结果
     * @property total_videos 匹配的视频总数
     * @property current_offset 当前偏移量
     * @property limit 每页限制数
     * @property videos 视频列表
     */
    @Serializable
    data class Response(
        val has_more: Boolean = false,
        val total_videos: Int = 0,
        val current_offset: Int = 0,
        val limit: Int = 0,
        val videos: List<Video> = emptyList()
    ) {
        /**
         * 视频详情数据模型。
         *
         * @property title 视频标题
         * @property keyword 搜索关键词
         * @property channel 频道名称
         * @property duration 视频时长（秒）
         * @property framerate 帧率
         * @property hd 是否高清
         * @property addtime 添加时间戳
         * @property viewnumber 观看次数
         * @property likes 点赞数
         * @property dislikes 踩数
         * @property video_url 视频文件 URL
         * @property embedded_url 嵌入播放器 URL
         * @property preview_url 预览图 URL
         * @property preview_video_url 预览视频 URL
         * @property isPublic 是否公开（JSON 字段名为 "public"）
         * @property vid 视频 ID
         * @property uid 用户 ID
         */
        @Serializable
        data class Video(
            val title: String = "",
            val keyword: String = "",
            val channel: String = "",
            val duration: Double = 0.0,
            val framerate: Double = 0.0,
            val hd: Boolean = false,
            val addtime: Int = 0,
            val viewnumber: Int = 0,
            val likes: Int = 0,
            val dislikes: Int = 0,
            val video_url: String = "",
            val embedded_url: String = "",
            val preview_url: String = "",
            val preview_video_url: String = "",
            @SerialName("public")
            val isPublic: Boolean = false,
            val vid: String = "",
            val uid: String = ""
        )
    }
}
