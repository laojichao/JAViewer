package io.github.javiewer.fragment

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

/**
 * 最新发布影片列表 Fragment，加载最新发布分页数据。
 */
class ReleasedFragment : MovieFragment() {
    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.getReleased(page)
}
