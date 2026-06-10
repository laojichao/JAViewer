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
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户配置仓库，统一管理收藏夹和用户偏好设置。
 *
 * 收藏夹数据优先使用 Room 数据库（[movieDao] / [actressDao]），
 * 配置数据优先使用 DataStore（[configDataStore]），
 * 同时保留对旧版 [Configurations] JSON 文件的兼容回退。
 *
 * @property configurations 旧版 JSON 配置对象，用于数据迁移和回退
 * @property movieDao 影片收藏 DAO
 * @property actressDao 女优收藏 DAO
 * @property configDataStore 键值对配置存储
 */
@Singleton
class ConfigRepository @Inject constructor(
    private val configurations: Configurations,
    private val movieDao: FavoriteMovieDao,
    private val actressDao: FavoriteActressDao,
    private val configDataStore: ConfigDataStore
) {
    companion object {
        private const val TAG = "ConfigRepository"
    }

    // --- Favorites (Room) ---

    /** 获取收藏影片的响应式数据流 */
    fun getStarredMoviesFlow(): Flow<List<Movie>> =
        movieDao.getAll().map { list -> list.map { it.toMovie() } }

    /** 获取收藏女优的响应式数据流 */
    fun getStarredActressesFlow(): Flow<List<Actress>> =
        actressDao.getAll().map { list -> list.map { it.toActress() } }

    /**
     * 同步获取收藏影片列表（阻塞当前线程）。
     * Room 查询失败时回退到旧版 JSON 配置。
     */
    fun getStarredMovies(): ArrayList<Movie> {
        return try {
            ArrayList(runBlocking(Dispatchers.IO) { movieDao.getAllSync().map { it.toMovie() } })
        } catch (e: Exception) {
            Log.w(TAG, "Room query failed, falling back to JSON", e)
            configurations.getStarredMovies()
        }
    }

    /**
     * 同步获取收藏女优列表（阻塞当前线程）。
     * Room 查询失败时回退到旧版 JSON 配置。
     */
    fun getStarredActresses(): ArrayList<Actress> {
        return try {
            ArrayList(runBlocking(Dispatchers.IO) { actressDao.getAllSync().map { it.toActress() } })
        } catch (e: Exception) {
            Log.w(TAG, "Room query failed, falling back to JSON", e)
            configurations.getStarredActresses()
        }
    }

    /**
     * 切换影片收藏状态（挂起版本）。
     *
     * @param movie 目标影片
     * @return true 表示已收藏，false 表示已取消收藏
     */
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

    /**
     * 切换影片收藏状态（同步阻塞版本）。
     *
     * @param movie 目标影片
     * @return true 表示已收藏，false 表示已取消收藏
     */
    fun toggleStarMovieSync(movie: Movie): Boolean {
        return runBlocking(Dispatchers.IO) { toggleStarMovie(movie) }
    }

    /**
     * 检查影片是否已收藏（挂起版本）。
     *
     * @param movie 目标影片
     * @return true 表示已收藏
     */
    suspend fun isMovieStarred(movie: Movie): Boolean =
        movieDao.isStarred(movie.code)

    /**
     * 检查影片是否已收藏（同步阻塞版本）。
     *
     * @param movie 目标影片
     * @return true 表示已收藏
     */
    fun isMovieStarredSync(movie: Movie): Boolean =
        runBlocking(Dispatchers.IO) { movieDao.isStarred(movie.code) }

    /**
     * 检查女优是否已收藏（同步阻塞版本）。
     *
     * @param actress 目标女优
     * @return true 表示已收藏
     */
    fun isActressStarredSync(actress: Actress): Boolean =
        runBlocking(Dispatchers.IO) { actressDao.isStarred(actress.name) }

    /**
     * 切换女优收藏状态。
     *
     * @param actress 目标女优
     * @return true 表示已收藏，false 表示已取消收藏
     */
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

    /**
     * 获取当前数据源。
     * 优先从 DataStore 读取，为空时回退到旧版 JSON 配置。
     * 从 DataStore 恢复时会尝试从全局数据源列表中匹配 legacies 字段。
     */
    fun getDataSource(): DataSource {
        return try {
            val prefs = runBlocking(Dispatchers.IO) { configDataStore.configValues.first() }
            if (prefs.dataSourceLink.isNotEmpty()) {
                // Try to find matching legacies from the global data sources list
                val legacies = io.github.javiewer.JAViewer.DATA_SOURCES
                    .firstOrNull { it.link == prefs.dataSourceLink }?.legacies
                DataSource(prefs.dataSourceName, legacies, prefs.dataSourceLink)
            } else {
                configurations.getDataSource()
            }
        } catch (e: Exception) {
            Log.w(TAG, "DataStore read failed, falling back to JSON", e)
            configurations.getDataSource()
        }
    }

    /**
     * 设置当前数据源，同时更新 JSON 配置文件和 DataStore。
     *
     * @param source 新的数据源
     */
    fun setDataSource(source: DataSource) {
        configurations.setDataSource(source)
        configurations.save()
        runBlocking(Dispatchers.IO) {
            configDataStore.setDataSource(source.name, source.link ?: "")
        }
    }

    /**
     * 获取下载计数器值。
     * 优先从 DataStore 读取，失败时回退到旧版 JSON 配置。
     */
    fun getDownloadCounter(): Long {
        return try {
            runBlocking(Dispatchers.IO) { configDataStore.configValues.first().downloadCounter }
        } catch (e: Exception) {
            Log.w(TAG, "DataStore read failed, falling back to JSON", e)
            configurations.getDownloadCounter()
        }
    }

    /**
     * 设置下载计数器值。
     *
     * @param counter 新的计数值
     */
    fun setDownloadCounter(counter: Long) {
        configurations.setDownloadCounter(counter)
        configurations.save()
        runBlocking(Dispatchers.IO) { configDataStore.setDownloadCounter(counter) }
    }

    /** 获取旧版配置对象引用 */
    fun getConfigurations() = configurations

    /** 保存旧版 JSON 配置到文件 */
    fun saveConfigurations() {
        configurations.save()
    }

    // --- Mappers ---

    private fun FavoriteMovieEntity.toMovie() = Movie(title, code, coverUrl, date, hot, link)
    private fun Movie.toEntity() = FavoriteMovieEntity(code, title, coverUrl, date, hot, link)

    private fun FavoriteActressEntity.toActress() = Actress(name, imageUrl, link)
    private fun Actress.toEntity() = FavoriteActressEntity(name, imageUrl, link)
}
