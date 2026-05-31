package io.github.javiewer.data.migration

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.javiewer.Configurations
import io.github.javiewer.JAViewer
import io.github.javiewer.data.datastore.ConfigDataStore
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonToRoomMigrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val movieDao: FavoriteMovieDao,
    private val actressDao: FavoriteActressDao,
    private val configDataStore: ConfigDataStore
) {
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
