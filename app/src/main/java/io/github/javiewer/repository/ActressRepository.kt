package io.github.javiewer.repository

import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.network.BasicService
import io.github.javiewer.network.provider.AVMOProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActressRepository @Inject constructor(
    private val service: BasicService
) {
    fun getActresses(page: Int) = service.getActresses(page)
    fun parseActresses(html: String): List<Actress> = AVMOProvider.parseActresses(html)
}
