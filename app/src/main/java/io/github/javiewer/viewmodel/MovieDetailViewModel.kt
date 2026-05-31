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

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val detail: StateFlow<UiState<MovieDetail>> = _detail

    private val _starred = MutableStateFlow(false)
    val starred: StateFlow<Boolean> = _starred

    fun loadDetail(link: String, movieTitle: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            try {
                val detail = movieRepository.getMovieDetail(link)
                detail.headers.add(0, Header.create("影片名", movieTitle, null))
                _detail.value = UiState.Success(detail)
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun isMovieStarred(movie: Movie): Boolean =
        configRepository.isMovieStarredSync(movie)

    fun toggleStarMovie(movie: Movie) {
        viewModelScope.launch {
            val isStarred = configRepository.toggleStarMovie(movie)
            _starred.value = isStarred
        }
    }
}
