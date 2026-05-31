package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.repository.ConfigRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    configRepository: ConfigRepository
) : ViewModel() {

    val movies: StateFlow<List<Movie>> = configRepository.getStarredMoviesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val actresses: StateFlow<List<Actress>> = configRepository.getStarredActressesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
