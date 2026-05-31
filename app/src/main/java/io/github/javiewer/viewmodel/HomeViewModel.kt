package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.repository.ActressRepository
import io.github.javiewer.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val actressRepository: ActressRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _actresses = MutableStateFlow<List<Actress>>(emptyList())
    val actresses: StateFlow<List<Actress>> = _actresses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab

    private var currentPage = 0
    private var isEnd = false

    init {
        loadTab(0)
    }

    fun loadTab(tab: Int) {
        _currentTab.value = tab
        currentPage = 0
        isEnd = false
        _movies.value = emptyList()
        _actresses.value = emptyList()
        loadNextPage()
    }

    fun refresh() {
        currentPage = 0
        isEnd = false
        _movies.value = emptyList()
        _actresses.value = emptyList()
        loadNextPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || isEnd) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val page = currentPage + 1
                when (_currentTab.value) {
                    0 -> {
                        val newMovies = movieRepository.getMovies(page)
                        if (newMovies.isEmpty()) {
                            isEnd = true
                        } else {
                            _movies.value = _movies.value + newMovies
                            currentPage = page
                        }
                    }
                    1 -> {
                        val newMovies = movieRepository.getReleased(page)
                        if (newMovies.isEmpty()) {
                            isEnd = true
                        } else {
                            _movies.value = _movies.value + newMovies
                            currentPage = page
                        }
                    }
                    2 -> {
                        val newMovies = movieRepository.getPopular(page)
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
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
