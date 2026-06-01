package io.github.javiewer.fragment

import android.os.Bundle
import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

/**
 * 通用影片列表 Fragment，根据传入的 URL 加载影片数据。
 *
 * 用于搜索结果、类别影片列表、女优作品列表等场景。
 * 通过 arguments 的 "link" 字段传入目标 URL。
 */
class MovieListFragment : MovieFragment() {

    private var link: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = arguments?.getString("link") ?: ""
    }

    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.get("$link/page/$page")
}
