package io.github.javiewer.repository

import io.github.javiewer.JAViewer
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.adapter.item.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor() {

    fun getConfigurations() = JAViewer.CONFIGURATIONS

    fun getStarredMovies(): ArrayList<Movie> =
        JAViewer.CONFIGURATIONS?.getStarredMovies() ?: arrayListOf()

    fun getStarredActresses(): ArrayList<Actress> =
        JAViewer.CONFIGURATIONS?.getStarredActresses() ?: arrayListOf()

    fun getDataSource(): DataSource =
        JAViewer.CONFIGURATIONS?.getDataSource() ?: DataSource.AVMO

    fun setDataSource(source: DataSource) {
        JAViewer.CONFIGURATIONS?.setDataSource(source)
        JAViewer.CONFIGURATIONS?.save()
    }

    fun saveConfigurations() {
        JAViewer.CONFIGURATIONS?.save()
    }

    fun getDownloadCounter(): Long =
        JAViewer.CONFIGURATIONS?.getDownloadCounter() ?: 0

    fun setDownloadCounter(counter: Long) {
        JAViewer.CONFIGURATIONS?.setDownloadCounter(counter)
        JAViewer.CONFIGURATIONS?.save()
    }

    fun toggleStarMovie(movie: Movie): Boolean {
        val starred = getStarredMovies()
        return if (starred.contains(movie)) {
            starred.remove(movie)
            JAViewer.CONFIGURATIONS?.save()
            false
        } else {
            starred.reverse()
            starred.add(movie)
            starred.reverse()
            JAViewer.CONFIGURATIONS?.save()
            true
        }
    }

    fun isMovieStarred(movie: Movie): Boolean =
        getStarredMovies().contains(movie)

    fun toggleStarActress(actress: Actress): Boolean {
        val starred = getStarredActresses()
        return if (starred.contains(actress)) {
            starred.remove(actress)
            JAViewer.CONFIGURATIONS?.save()
            false
        } else {
            starred.reverse()
            starred.add(actress)
            starred.reverse()
            JAViewer.CONFIGURATIONS?.save()
            true
        }
    }
}
