package io.github.javiewer.data.migration

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javiewer.JAViewer
import io.github.javiewer.data.datastore.ConfigDataStore
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * JSON 到 Room 的数据迁移器。
 *
 * 在应用首次升级到 Room 版本时，将旧版 `configurations.json` 中的
 * 收藏数据和配置数据迁移到 Room 数据库和 DataStore。
 *
 * 迁移完成后标记 DataStore 中已有数据源配置，后续启动不再重复迁移。
 *
 * @property context 应用上下文
 * @property movieDao 影片收藏 DAO
 * @property actressDao 女优收藏 DAO
 * @property configDataStore 键值对配置存储
 */
@Singleton
class JsonToRoomMigrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val movieDao: FavoriteMovieDao,
    private val actressDao: FavoriteActressDao,
    private val configDataStore: ConfigDataStore
) {
    /**
     * 检查并执行数据迁移。
     *
     * 通过 DataStore 中的专用迁移标记判断是否已迁移，
     * 避免重复迁移。迁移内容包括收藏影片、收藏女优、数据源和下载计数器。
     * 迁移过程中任何错误都会被记录，不会导致数据丢失。
     */
    suspend fun migrateIfNeeded() {
        if (configDataStore.isMigrationCompleted()) return

        val config = JAViewer.CONFIGURATIONS
        if (config == null) {
            Log.w(TAG, "CONFIGURATIONS is null, skipping migration")
            return
        }

        Log.i(TAG, "Starting JSON-to-Room migration")
        var migrationSucceeded = true

        try {
            // Migrate starred movies
            val movies = config.getStarredMovies()
            for (movie in movies) {
                movieDao.insert(
                    FavoriteMovieEntity(
                        code = movie.code,
                        title = movie.title,
                        coverUrl = movie.coverUrl,
                        date = movie.date,
                        hot = movie.hot,
                        link = movie.link
                    )
                )
            }
            Log.i(TAG, "Migrated ${movies.size} starred movies")

            // Migrate starred actresses
            val actresses = config.getStarredActresses()
            for (actress in actresses) {
                actressDao.insert(
                    FavoriteActressEntity(
                        name = actress.name,
                        imageUrl = actress.imageUrl,
                        link = actress.link
                    )
                )
            }
            Log.i(TAG, "Migrated ${actresses.size} starred actresses")

            // Migrate config values
            val ds = config.getDataSource()
            configDataStore.setDataSource(ds.name, ds.link ?: "")
            configDataStore.setDownloadCounter(config.getDownloadCounter())
        } catch (e: Exception) {
            Log.e(TAG, "Migration failed, will retry next launch", e)
            migrationSucceeded = false
        }

        // 只有全部成功才标记迁移完成
        if (migrationSucceeded) {
            configDataStore.setMigrationCompleted()
            Log.i(TAG, "Migration completed successfully")
        }
    }

    companion object {
        private const val TAG = "JsonToRoomMigrator"
    }
}
