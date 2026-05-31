package io.github.javiewer.fragment

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

class PopularFragment : MovieFragment() {
    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.getPopular(page)
}
