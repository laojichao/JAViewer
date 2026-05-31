package io.github.javiewer.repository

import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.network.BasicService
import io.github.javiewer.network.provider.AVMOProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val service: BasicService
) {
    suspend fun getMovies(page: Int): List<Movie> {
        val html = service.getHomePage(page).string()
        return AVMOProvider.parseMovies(html)
    }

    suspend fun getReleased(page: Int): List<Movie> {
        val html = service.getReleased(page).string()
        return AVMOProvider.parseMovies(html)
    }

    suspend fun getPopular(page: Int): List<Movie> {
        val html = service.getPopular(page).string()
        return AVMOProvider.parseMovies(html)
    }

    suspend fun getMoviesFromUrl(url: String): List<Movie> {
        val html = service.get(url).string()
        return AVMOProvider.parseMovies(html)
    }

    suspend fun getMovieDetail(url: String): MovieDetail {
        val html = service.get(url).string()
        return AVMOProvider.parseMoviesDetail(html)
    }

    suspend fun getGenres(): LinkedHashMap<String, List<Genre>> {
        val html = service.getGenre().string()
        return AVMOProvider.parseGenres(html)
    }
}
