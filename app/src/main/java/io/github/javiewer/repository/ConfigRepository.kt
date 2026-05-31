package io.github.javiewer.repository

import io.github.javiewer.Configurations
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.data.datastore.ConfigDataStore
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val configurations: Configurations,
    private val movieDao: FavoriteMovieDao,
    private val actressDao: FavoriteActressDao,
    private val configDataStore: ConfigDataStore
) {

    // --- Favorites (Room) ---

    fun getStarredMoviesFlow(): Flow<List<Movie>> =
        movieDao.getAll().map { list -> list.map { it.toMovie() } }

    fun getStarredActressesFlow(): Flow<List<Actress>> =
        actressDao.getAll().map { list -> list.map { it.toActress() } }

    fun getStarredMovies(): ArrayList<Movie> {
        return try {
            ArrayList(runBlocking { movieDao.getAllSync().map { it.toMovie() } })
        } catch (_: Exception) {
            configurations.getStarredMovies()
        }
    }

    fun getStarredActresses(): ArrayList<Actress> {
        return try {
            ArrayList(runBlocking { actressDao.getAllSync().map { it.toActress() } })
        } catch (_: Exception) {
            configurations.getStarredActresses()
        }
    }

    suspend fun toggleStarMovie(movie: Movie): Boolean {
        val isStarred = movieDao.isStarred(movie.code)
        return if (isStarred) {
            movieDao.deleteByCode(movie.code)
            false
        } else {
            movieDao.insert(movie.toEntity())
            true
        }
    }

    fun toggleStarMovieSync(movie: Movie): Boolean {
        return runBlocking { toggleStarMovie(movie) }
    }

    suspend fun isMovieStarred(movie: Movie): Boolean =
        movieDao.isStarred(movie.code)

    fun isMovieStarredSync(movie: Movie): Boolean =
        runBlocking { movieDao.isStarred(movie.code) }

    suspend fun toggleStarActress(actress: Actress): Boolean {
        val isStarred = actressDao.isStarred(actress.name)
        return if (isStarred) {
            actressDao.deleteByName(actress.name)
            false
        } else {
            actressDao.insert(actress.toEntity())
            true
        }
    }

    // --- Config (DataStore + fallback) ---

    fun getDataSource(): DataSource {
        return try {
            val prefs = runBlocking { configDataStore.configValues.first() }
            if (prefs.dataSourceLink.isNotEmpty()) {
                DataSource(prefs.dataSourceName, null, prefs.dataSourceLink)
            } else {
                configurations.getDataSource()
            }
        } catch (_: Exception) {
            configurations.getDataSource()
        }
    }

    fun setDataSource(source: DataSource) {
        configurations.setDataSource(source)
        configurations.save()
        runBlocking {
            configDataStore.setDataSource(source.name, source.link ?: "")
        }
    }

    fun getDownloadCounter(): Long {
        return try {
            runBlocking { configDataStore.configValues.first().downloadCounter }
        } catch (_: Exception) {
            configurations.getDownloadCounter()
        }
    }

    fun setDownloadCounter(counter: Long) {
        configurations.setDownloadCounter(counter)
        configurations.save()
        runBlocking { configDataStore.setDownloadCounter(counter) }
    }

    fun getConfigurations() = configurations

    fun saveConfigurations() {
        configurations.save()
    }

    // --- Mappers ---

    private fun FavoriteMovieEntity.toMovie() = Movie(title, code, coverUrl, date, hot, link)
    private fun Movie.toEntity() = FavoriteMovieEntity(code, title, coverUrl, date, hot, link)

    private fun FavoriteActressEntity.toActress() = Actress(name, imageUrl, link)
    private fun Actress.toEntity() = FavoriteActressEntity(name, imageUrl, link)
}
