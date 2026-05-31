package io.github.javiewer.fragment

import io.github.javiewer.JAViewer
import okhttp3.ResponseBody

class ReleasedFragment : MovieFragment() {
    override suspend fun loadData(page: Int): ResponseBody? = JAViewer.SERVICE?.getReleased(page)
}
