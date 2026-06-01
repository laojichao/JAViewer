package io.github.javiewer.di

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.javiewer.Configurations
import io.github.javiewer.data.db.JaviewerDatabase
import io.github.javiewer.data.db.dao.FavoriteActressDao
import io.github.javiewer.data.db.dao.FavoriteMovieDao
import java.io.File
import javax.inject.Singleton

/**
 * Hilt 应用级依赖模块，提供全局单例依赖。
 *
 * 包括 Gson 实例、存储目录、旧版配置对象、Room 数据库及 DAO。
 * 安装在 [SingletonComponent] 中，生命周期与应用一致。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** 提供 Gson 实例，用于 JSON 序列化/反序列化 */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    /**
     * 提供应用外部存储目录。
     * Android Q+ 使用应用专属目录，旧版本使用公共外部存储。
     */
    @Provides
    @Singleton
    fun provideStorageDir(@ApplicationContext context: Context): File {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(null), "JAViewer/")
        } else {
            @Suppress("DEPRECATION")
            File(Environment.getExternalStorageDirectory(), "JAViewer/")
        }
        dir.mkdirs()
        return dir
    }

    /**
     * 提供旧版 [Configurations] 实例，从 JSON 文件加载。
     * 用于数据迁移回退和部分旧代码兼容。
     */
    @Provides
    @Singleton
    fun provideConfigurations(storageDir: File): Configurations {
        val configFile = File(storageDir, "configurations.json")
        return Configurations.load(configFile)
    }

    /** 提供 Room 数据库单例 */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JaviewerDatabase {
        return Room.databaseBuilder(
            context,
            JaviewerDatabase::class.java,
            "javiewer.db"
        ).build()
    }

    /** 提供收藏影片 DAO */
    @Provides
    fun provideFavoriteMovieDao(db: JaviewerDatabase): FavoriteMovieDao = db.favoriteMovieDao()

    /** 提供收藏女优 DAO */
    @Provides
    fun provideFavoriteActressDao(db: JaviewerDatabase): FavoriteActressDao = db.favoriteActressDao()
}
