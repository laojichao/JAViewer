package io.github.javiewer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import io.github.javiewer.data.db.entity.FavoriteActressEntity
import io.github.javiewer.data.db.entity.FavoriteMovieEntity

/**
 * Room 数据库定义，包含收藏影片和收藏女优两张表。
 *
 * 版本号为 1。通过 Hilt 依赖注入提供单例。
 * 导出 Schema 到 `app/schemas/` 目录以便未来迁移测试。
 */
@Database(
    entities = [FavoriteMovieEntity::class, FavoriteActressEntity::class],
    version = 1,
    exportSchema = true
)
abstract class JaviewerDatabase : RoomDatabase() {
    /** 获取收藏影片 DAO */
    abstract fun favoriteMovieDao(): FavoriteMovieDao

    /** 获取收藏女优 DAO */
    abstract fun favoriteActressDao(): FavoriteActressDao
}
