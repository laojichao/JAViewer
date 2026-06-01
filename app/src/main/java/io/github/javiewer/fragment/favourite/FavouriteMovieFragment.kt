package io.github.javiewer.fragment.favourite

import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.adapter.MovieAdapter
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.view.decoration.MovieItemDecoration
import javax.inject.Inject

/**
 * 收藏影片列表 Fragment，展示用户收藏的影片。
 *
 * 使用 Hilt 注入 [ConfigRepository] 获取收藏数据。
 * 不显示热门标记（showIfHot = false）。
 */
@AndroidEntryPoint
class FavouriteMovieFragment : FavouriteFragment<Movie>() {

    @Inject lateinit var configRepository: ConfigRepository

    override fun adapter(): ItemAdapter<*, *> {
        return MovieAdapter(configRepository.getStarredMovies(), activity).apply {
            showIfHot = false
        }
    }

    override fun decoration(): RecyclerView.ItemDecoration = MovieItemDecoration()
}
