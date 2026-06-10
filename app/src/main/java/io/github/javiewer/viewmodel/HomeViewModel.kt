package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.repository.ActressRepository
import io.github.javiewer.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 ViewModel，驱动 Compose HomeScreen 的数据加载。
 *
 * 支持多标签页（主页/已发布/热门/女优）的数据加载和无限滚动分页，
 * 通过 [movies]、[actresses]、[isLoading]、[currentTab] 暴露 UI 状态。
 *
 * @property movieRepository 影片数据仓库
 * @property actressRepository 女优数据仓库
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val actressRepository: ActressRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    /** 当前标签页的影片列表 */
    val movies: StateFlow<List<Movie>> = _movies

    private val _actresses = MutableStateFlow<List<Actress>>(emptyList())
    /** 当前标签页的女优列表 */
    val actresses: StateFlow<List<Actress>> = _actresses

    private val _isLoading = MutableStateFlow(false)
    /** 是否正在加载数据 */
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    /** 加载错误信息，null 表示无错误 */
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentTab = MutableStateFlow(0)
    /** 当前选中的标签页索引：0=主页, 1=已发布, 2=热门, 3=女优 */
    val currentTab: StateFlow<Int> = _currentTab

    private var currentPage = 0
    private var isEnd = false
    private var loadJob: Job? = null

    init {
        loadTab(0)
    }

    /**
     * 切换标签页并重新加载数据。
     *
     * @param tab 标签页索引：0=主页, 1=已发布, 2=热门, 3=女优
     */
    fun loadTab(tab: Int) {
        loadJob?.cancel()
        _isLoading.value = false
        _errorMessage.value = null
        _currentTab.value = tab
        currentPage = 0
        isEnd = false
        _movies.value = emptyList()
        _actresses.value = emptyList()
        loadNextPage()
    }

    /** 下拉刷新，重置分页状态并重新加载 */
    fun refresh() {
        loadJob?.cancel()
        _isLoading.value = false
        _errorMessage.value = null
        currentPage = 0
        isEnd = false
        _movies.value = emptyList()
        _actresses.value = emptyList()
        loadNextPage()
    }

    /** 加载下一页数据，滚动到底部时自动触发 */
    fun loadNextPage() {
        if (_isLoading.value || isEnd) return
        _isLoading.value = true

        loadJob = viewModelScope.launch {
            try {
                val page = currentPage + 1
                when (_currentTab.value) {
                    0, 1, 2 -> {
                        val newMovies = when (_currentTab.value) {
                            0 -> movieRepository.getMovies(page)
                            1 -> movieRepository.getReleased(page)
                            else -> movieRepository.getPopular(page)
                        }
                        if (newMovies.isEmpty()) {
                            isEnd = true
                        } else {
                            _movies.value = _movies.value + newMovies
                            currentPage = page
                        }
                    }
                    3 -> {
                        val newActresses = actressRepository.getActresses(page)
                        if (newActresses.isEmpty()) {
                            isEnd = true
                        } else {
                            _actresses.value = _actresses.value + newActresses
                            currentPage = page
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "加载失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
