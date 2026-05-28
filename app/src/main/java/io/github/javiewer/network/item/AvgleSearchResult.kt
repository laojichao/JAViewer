package io.github.javiewer.network.item

import com.google.gson.annotations.SerializedName

data class AvgleSearchResult(
    val success: Boolean = false,
    val response: Response = Response()
) {
    data class Response(
        val has_more: Boolean = false,
        val total_videos: Int = 0,
        val current_offset: Int = 0,
        val limit: Int = 0,
        val videos: List<Video> = emptyList()
    ) {
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
            @SerializedName("public")
            val isPublic: Boolean = false,
            val vid: String = "",
            val uid: String = ""
        )
    }
}
