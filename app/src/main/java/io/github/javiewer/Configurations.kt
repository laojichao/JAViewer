package io.github.javiewer

import com.google.gson.Gson
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.adapter.item.Movie
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class Configurations {

    @Transient
    private var file: File? = null

    @Transient
    private val gson: Gson = Gson()

    private var starred_movies: ArrayList<Movie>? = null
    private var starred_actresses: ArrayList<Actress>? = null
    private var data_source: DataSource? = null
    private var show_ads: Boolean = false
    private var download_counter: Long = 0

    fun getStarredMovies(): ArrayList<Movie> {
        return synchronized(this) {
            if (starred_movies == null) starred_movies = arrayListOf()
            starred_movies!!
        }
    }

    fun getStarredActresses(): ArrayList<Actress> {
        return synchronized(this) {
            if (starred_actresses == null) starred_actresses = arrayListOf()
            starred_actresses!!
        }
    }

    fun getDataSource(): DataSource {
        return synchronized(this) {
            if (data_source == null) data_source = JAViewer.DATA_SOURCES.firstOrNull() ?: DataSource.AVMO
            data_source!!
        }
    }

    fun setDataSource(source: DataSource) {
        this.data_source = source
    }

    fun save() {
        val f = file ?: return
        try {
            FileWriter(f).use { writer ->
                gson.toJson(this, writer)
                writer.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAds(): Boolean = show_ads

    fun setShowAds(show_ads: Boolean) {
        this.show_ads = show_ads
    }

    fun getDownloadCounter(): Long = download_counter

    fun setDownloadCounter(counter: Long) {
        this.download_counter = counter
    }

    companion object {
        @JvmStatic
        fun load(file: File): Configurations {
            val config: Configurations? = try {
                FileReader(file).use { reader ->
                    com.google.gson.stream.JsonReader(reader).use { jsonReader ->
                        JAViewer.parseJson(Configurations::class.java, jsonReader)
                    }
                }
            } catch (_: Exception) {
                null
            }
            return (config ?: Configurations()).apply { this.file = file }
        }
    }
}
