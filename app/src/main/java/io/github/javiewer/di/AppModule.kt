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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

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

    @Provides
    @Singleton
    fun provideConfigurations(storageDir: File): Configurations {
        val configFile = File(storageDir, "configurations.json")
        return Configurations.load(configFile)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JaviewerDatabase {
        return Room.databaseBuilder(
            context,
            JaviewerDatabase::class.java,
            "javiewer.db"
        ).build()
    }

    @Provides
    fun provideFavoriteMovieDao(db: JaviewerDatabase): FavoriteMovieDao = db.favoriteMovieDao()

    @Provides
    fun provideFavoriteActressDao(db: JaviewerDatabase): FavoriteActressDao = db.favoriteActressDao()
}
