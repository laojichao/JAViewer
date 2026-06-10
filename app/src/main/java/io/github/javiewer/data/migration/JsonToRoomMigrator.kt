package io.github.javiewer.data.migration

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javiewer.JAViewer
import io.github.javiewer.data.datastore.ConfigDataStore
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.first
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
     * 通过检查 DataStore 中是否已有数据源配置来判断是否已迁移，
     * 避免重复迁移。迁移内容包括收藏影片、收藏女优、数据源和下载计数器。
     */
    suspend fun migrateIfNeeded() {
        val prefs = configDataStore.configValues.first()
        if (prefs.dataSourceName.isNotEmpty()) return // Already migrated

        val config = JAViewer.CONFIGURATIONS ?: return

        // Migrate starred movies
        for (movie in config.getStarredMovies()) {
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

        // Migrate starred actresses
        for (actress in config.getStarredActresses()) {
            actressDao.insert(
                FavoriteActressEntity(
                    name = actress.name,
                    imageUrl = actress.imageUrl,
                    link = actress.link
                )
            )
        }

        // Migrate config values
        val ds = config.getDataSource()
        configDataStore.setDataSource(ds.name, ds.link ?: "")
        configDataStore.setDownloadCounter(config.getDownloadCounter())
    }
}
