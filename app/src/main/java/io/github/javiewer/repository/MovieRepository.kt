package io.github.javiewer.repository

import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.network.BasicService
import io.github.javiewer.network.provider.AVMOProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 影片数据仓库，封装 [BasicService] 的网络请求和 [AVMOProvider] 的 HTML 解析。
 *
 * 所有方法均为挂起函数，在 IO 线程执行网络请求并返回解析后的领域模型。
 *
 * @property service Retrofit 服务接口，用于发起 HTTP 请求
 */
@Singleton
class MovieRepository @Inject constructor(
    private val service: BasicService
) {
    /**
     * 获取首页影片列表。
     *
     * @param page 页码，从 1 开始
     * @return 当前页的影片列表
     */
    suspend fun getMovies(page: Int): List<Movie> {
        val html = service.getHomePage(page).string()
        return AVMOProvider.parseMovies(html)
    }

    /**
     * 获取最新发布影片列表。
     *
     * @param page 页码
     * @return 当前页的影片列表
     */
    suspend fun getReleased(page: Int): List<Movie> {
        val html = service.getReleased(page).string()
        return AVMOProvider.parseMovies(html)
    }

    /**
     * 获取热门影片列表。
     *
     * @param page 页码
     * @return 当前页的影片列表
     */
    suspend fun getPopular(page: Int): List<Movie> {
        val html = service.getPopular(page).string()
        return AVMOProvider.parseMovies(html)
    }

    /**
     * 从指定 URL 获取影片列表（用于搜索、女优影片列表等）。
     *
     * @param url 完整的影片列表页 URL
     * @return 解析后的影片列表
     */
    suspend fun getMoviesFromUrl(url: String): List<Movie> {
        val html = service.get(url).string()
        return AVMOProvider.parseMovies(html)
    }

    /**
     * 获取影片详情。
     *
     * @param url 影片详情页 URL
     * @return 解析后的 [MovieDetail] 实例
     */
    suspend fun getMovieDetail(url: String): MovieDetail {
        val html = service.get(url).string()
        return AVMOProvider.parseMoviesDetail(html)
    }

    /**
     * 获取所有类别，按分组返回。
     *
     * @return 以类别组标题为 key、类别列表为 value 的有序 Map
     */
    suspend fun getGenres(): LinkedHashMap<String, List<Genre>> {
        val html = service.getGenre().string()
        return AVMOProvider.parseGenres(html)
    }
}
