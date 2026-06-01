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

/**
 * 收藏夹 ViewModel，将 Room 数据库中的收藏数据暴露为响应式 StateFlow。
 *
 * [movies] 和 [actresses] 会自动响应数据库变更，UI 层通过 collect 即可实时更新。
 *
 * @property movies 收藏影片列表的响应式数据流
 * @property actresses 收藏女优列表的响应式数据流
 */
@HiltViewModel
class FavouriteViewModel @Inject constructor(
    configRepository: ConfigRepository
) : ViewModel() {

    val movies: StateFlow<List<Movie>> = configRepository.getStarredMoviesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val actresses: StateFlow<List<Actress>> = configRepository.getStarredActressesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
