package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.adapter.item.MovieDetail.Header
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.repository.MovieRepository
import io.github.javiewer.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 影片详情页 ViewModel，管理影片详情加载和收藏状态。
 *
 * 通过 [detail] 暴露加载状态（Loading/Success/Error），
 * 通过 [starred] 暴露收藏状态变更。
 *
 * @property movieRepository 影片数据仓库
 * @property configRepository 用户配置仓库（收藏管理）
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    /** 影片详情的加载状态 */
    val detail: StateFlow<UiState<MovieDetail>> = _detail

    private val _starred = MutableStateFlow(false)
    /** 影片收藏状态变更事件 */
    val starred: StateFlow<Boolean> = _starred

    /**
     * 加载影片详情。
     *
     * @param link 影片详情页 URL
     * @param movieTitle 影片标题，插入到详情头部
     */
    fun loadDetail(link: String, movieTitle: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            try {
                val detail = movieRepository.getMovieDetail(link)
                // Create a copy with the title header prepended to avoid mutating shared state
                val headersWithTitle = mutableListOf(Header.create("影片名", movieTitle, null))
                headersWithTitle.addAll(detail.headers)
                val detailWithTitle = detail.copy(headers = headersWithTitle)
                _detail.value = UiState.Success(detailWithTitle)
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * 同步检查影片是否已收藏。
     *
     * @param movie 目标影片
     * @return true 表示已收藏
     */
    fun isMovieStarred(movie: Movie): Boolean =
        configRepository.isMovieStarredSync(movie)

    /**
     * 切换影片收藏状态，结果通过 [starred] 发出。
     *
     * @param movie 目标影片
     */
    fun toggleStarMovie(movie: Movie) {
        viewModelScope.launch {
            val isStarred = configRepository.toggleStarMovie(movie)
            _starred.value = isStarred
        }
    }
}
