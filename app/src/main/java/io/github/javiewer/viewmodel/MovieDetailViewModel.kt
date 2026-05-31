package io.github.javiewer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.adapter.item.MovieDetail.Header
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.repository.MovieRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _detail = MutableLiveData<MovieDetail>()
    val detail: LiveData<MovieDetail> = _detail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _starred = MutableLiveData<Boolean>()
    val starred: LiveData<Boolean> = _starred

    fun loadDetail(link: String, movieTitle: String) {
        movieRepository.get(link).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    response.body()?.close()
                    return
                }
                try {
                    val body = response.body()?.string()
                    if (body == null) {
                        response.body()?.close()
                        return
                    }
                    val detail = movieRepository.parseMovieDetail(body)
                    detail.headers.add(0, Header.create("影片名", movieTitle, null))
                    _detail.postValue(detail)
                } catch (e: Exception) {
                    _error.postValue(e.message ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _error.postValue(t.message ?: "Network error")
            }
        })
    }

    fun isMovieStarred(movie: Movie): Boolean =
        configRepository.isMovieStarred(movie)

    fun toggleStarMovie(movie: Movie) {
        val isStarred = configRepository.toggleStarMovie(movie)
        _starred.postValue(isStarred)
    }
}
