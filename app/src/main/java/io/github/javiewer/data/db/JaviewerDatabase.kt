package io.github.javiewer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity

@Database(
    entities = [FavoriteMovieEntity::class, FavoriteActressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JaviewerDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun favoriteActressDao(): FavoriteActressDao
}
