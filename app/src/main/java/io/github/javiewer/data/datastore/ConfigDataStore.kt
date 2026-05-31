package io.github.javiewer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.configDataStore: DataStore<Preferences> by preferencesDataStore(name = "config")

data class ConfigValues(
    val dataSourceName: String = "",
    val dataSourceLink: String = "",
    val downloadCounter: Long = 0
)

@Singleton
class ConfigDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore get() = context.configDataStore

    companion object {
        val KEY_DATA_SOURCE_NAME = stringPreferencesKey("data_source_name")
        val KEY_DATA_SOURCE_LINK = stringPreferencesKey("data_source_link")
        val KEY_DOWNLOAD_COUNTER = longPreferencesKey("download_counter")
    }

    val configValues: Flow<ConfigValues> = dataStore.data.map { prefs ->
        ConfigValues(
            dataSourceName = prefs[KEY_DATA_SOURCE_NAME] ?: "",
            dataSourceLink = prefs[KEY_DATA_SOURCE_LINK] ?: "",
            downloadCounter = prefs[KEY_DOWNLOAD_COUNTER] ?: 0
        )
    }

    suspend fun setDataSource(name: String, link: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DATA_SOURCE_NAME] = name
            prefs[KEY_DATA_SOURCE_LINK] = link
        }
    }

    suspend fun setDownloadCounter(counter: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_DOWNLOAD_COUNTER] = counter
        }
    }
}
