package io.github.javiewer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.repository.ConfigRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _switchSourceEvent = MutableLiveData<DataSource>()
    val switchSourceEvent: LiveData<DataSource> = _switchSourceEvent

    fun getDataSource(): DataSource = configRepository.getDataSource()

    fun switchDataSource(newSource: DataSource) {
        configRepository.setDataSource(newSource)
    }
}
