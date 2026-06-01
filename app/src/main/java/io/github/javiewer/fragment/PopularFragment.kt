package io.github.javiewer.fragment

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

/**
 * 热门影片列表 Fragment，加载热门分页数据。
 */
class PopularFragment : MovieFragment() {
    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.getPopular(page)
}
