package io.github.javiewer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.repository.ConfigRepository
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _moviesUpdated = MutableLiveData<List<Movie>>()
    val moviesUpdated: LiveData<List<Movie>> = _moviesUpdated

    private val _actressesUpdated = MutableLiveData<List<Actress>>()
    val actressesUpdated: LiveData<List<Actress>> = _actressesUpdated

    fun getStarredMovies(): ArrayList<Movie> = configRepository.getStarredMovies()
    fun getStarredActresses(): ArrayList<Actress> = configRepository.getStarredActresses()

    fun notifyDataChanged() {
        _moviesUpdated.postValue(configRepository.getStarredMovies())
        _actressesUpdated.postValue(configRepository.getStarredActresses())
    }
}
