package io.github.javiewer.repository

import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.network.BasicService
import io.github.javiewer.network.provider.AVMOProvider
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val service: BasicService
) {
    fun getHomePage(page: Int) = service.getHomePage(page)
    fun getReleased(page: Int) = service.getReleased(page)
    fun getPopular(page: Int) = service.getPopular(page)
    fun get(url: String) = service.get(url)

    fun parseMovies(html: String): List<Movie> = AVMOProvider.parseMovies(html)
    fun parseMovieDetail(html: String): MovieDetail = AVMOProvider.parseMoviesDetail(html)
    fun parseGenres(html: String): LinkedHashMap<String, List<Genre>> = AVMOProvider.parseGenres(html)
    fun getGenre() = service.getGenre()
}
