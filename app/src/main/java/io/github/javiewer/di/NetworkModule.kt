package io.github.javiewer.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.javiewer.JAViewer
import io.github.javiewer.network.BasicService
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = JAViewer.httpClient

    @Provides
    @Singleton
    fun provideBasicService(): BasicService {
        return JAViewer.SERVICE ?: synchronized(NetworkModule::class.java) {
            if (JAViewer.SERVICE == null) {
                JAViewer.recreateService()
            }
            JAViewer.SERVICE ?: throw IllegalStateException("BasicService not initialized. Check data source configuration.")
        }
    }
}
