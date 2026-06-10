package io.github.javiewer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Context 扩展属性，提供 DataStore 实例 */
val Context.configDataStore: DataStore<Preferences> by preferencesDataStore(name = "config")

/**
 * DataStore 配置值的数据类。
 *
 * @property dataSourceName 当前数据源名称
 * @property dataSourceLink 当前数据源 URL
 * @property downloadCounter 下载计数器值
 */
data class ConfigValues(
    val dataSourceName: String = "",
    val dataSourceLink: String = "",
    val downloadCounter: Long = 0
)

/**
 * 基于 DataStore Preferences 的键值对配置存储。
 *
 * 用于持久化用户偏好设置（当前数据源、下载计数器），
 * 替代旧版 JSON 文件存储方案。
 *
 * @property context 应用上下文
 */
@Singleton
class ConfigDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore get() = context.configDataStore

    companion object {
        /** 数据源名称的偏好键 */
        val KEY_DATA_SOURCE_NAME = stringPreferencesKey("data_source_name")

        /** 数据源 URL 的偏好键 */
        val KEY_DATA_SOURCE_LINK = stringPreferencesKey("data_source_link")

        /** 下载计数器的偏好键 */
        val KEY_DOWNLOAD_COUNTER = longPreferencesKey("download_counter")

        /** JSON 到 Room 迁移完成标记 */
        val KEY_MIGRATION_COMPLETED = booleanPreferencesKey("migration_completed")
    }

    /** 配置值的响应式数据流 */
    val configValues: Flow<ConfigValues> = dataStore.data.map { prefs ->
        ConfigValues(
            dataSourceName = prefs[KEY_DATA_SOURCE_NAME] ?: "",
            dataSourceLink = prefs[KEY_DATA_SOURCE_LINK] ?: "",
            downloadCounter = prefs[KEY_DOWNLOAD_COUNTER] ?: 0
        )
    }

    /** 检查 JSON 到 Room 迁移是否已完成 */
    suspend fun isMigrationCompleted(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[KEY_MIGRATION_COMPLETED] ?: false
        }.first()
    }

    /** 标记 JSON 到 Room 迁移已完成 */
    suspend fun setMigrationCompleted() {
        dataStore.edit { prefs ->
            prefs[KEY_MIGRATION_COMPLETED] = true
        }
    }

    /**
     * 设置当前数据源。
     *
     * @param name 数据源名称
     * @param link 数据源 URL
     */
    suspend fun setDataSource(name: String, link: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DATA_SOURCE_NAME] = name
            prefs[KEY_DATA_SOURCE_LINK] = link
        }
    }

    /**
     * 设置下载计数器值。
     *
     * @param counter 新的计数值
     */
    suspend fun setDownloadCounter(counter: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_DOWNLOAD_COUNTER] = counter
        }
    }
}
