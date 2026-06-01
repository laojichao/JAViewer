package io.github.javiewer.fragment

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

/**
 * 首页影片列表 Fragment，加载主页分页数据。
 */
class HomeFragment : MovieFragment() {
    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.getHomePage(page)
}
