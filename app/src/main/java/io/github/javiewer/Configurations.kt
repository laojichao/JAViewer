package io.github.javiewer

import com.google.gson.Gson
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.adapter.item.Movie
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * 用户配置管理类，负责收藏夹和用户偏好的 JSON 文件持久化。
 *
 * 数据存储在外部存储的 `configurations.json` 文件中，使用 Gson 序列化。
 * 所有可变状态通过 [synchronized] 保证线程安全。
 *
 * **注意**：此类正在被 Room + DataStore 方案逐步替代，
 * 目前仍作为数据迁移的回退来源和部分旧代码的兼容层保留。
 *
 * @property starred_movies 收藏影片列表（懒初始化）
 * @property starred_actresses 收藏女优列表（懒初始化）
 * @property data_source 当前数据源
 * @property show_ads 是否显示广告
 * @property download_counter 下载计数器
 */
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

    /**
     * 获取收藏影片列表，懒初始化。
     *
     * @return 收藏影片列表，保证非 null
     */
    fun getStarredMovies(): ArrayList<Movie> {
        return synchronized(this) {
            if (starred_movies == null) starred_movies = arrayListOf()
            starred_movies!!
        }
    }

    /**
     * 获取收藏女优列表，懒初始化。
     *
     * @return 收藏女优列表，保证非 null
     */
    fun getStarredActresses(): ArrayList<Actress> {
        return synchronized(this) {
            if (starred_actresses == null) starred_actresses = arrayListOf()
            starred_actresses!!
        }
    }

    /**
     * 获取当前数据源，未设置时默认为 [DataSource.AVMO]。
     *
     * @return 当前数据源
     */
    fun getDataSource(): DataSource {
        return synchronized(this) {
            if (data_source == null) data_source = DataSource.AVMO
            data_source!!
        }
    }

    /**
     * 设置当前数据源。
     *
     * @param source 新的数据源
     */
    fun setDataSource(source: DataSource) {
        synchronized(this) {
            this.data_source = source
        }
    }

    /**
     * 将当前配置保存到 JSON 文件。
     * 使用 Gson 序列化并通过 FileWriter 写入磁盘。
     * 通过 [synchronized] 保证读取状态的一致性。
     */
    fun save() {
        val f = file ?: return
        synchronized(this) {
            try {
                FileWriter(f).use { writer ->
                    gson.toJson(this, writer)
                    writer.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** 是否显示广告 */
    fun showAds(): Boolean = synchronized(this) { show_ads }

    /** 设置是否显示广告 */
    fun setShowAds(show_ads: Boolean) {
        synchronized(this) { this.show_ads = show_ads }
    }

    /** 获取下载计数器值 */
    fun getDownloadCounter(): Long = synchronized(this) { download_counter }

    /**
     * 设置下载计数器值。
     *
     * @param counter 新的计数值
     */
    fun setDownloadCounter(counter: Long) {
        synchronized(this) { this.download_counter = counter }
    }

    companion object {
        /**
         * 从 JSON 文件加载配置，文件不存在或解析失败时返回空配置。
         *
         * @param file 配置文件路径
         * @return 加载后的 [Configurations] 实例，已绑定文件路径
         */
        @JvmStatic
        fun load(file: File): Configurations {
            val config: Configurations? = try {
                FileReader(file).use { reader ->
                    com.google.gson.stream.JsonReader(reader).use { jsonReader ->
                        Gson().fromJson(jsonReader, Configurations::class.java)
                    }
                }
            } catch (_: Exception) {
                null
            }
            return (config ?: Configurations()).apply { this.file = file }
        }
    }
}
