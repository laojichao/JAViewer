package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.repository.ConfigRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    fun getDataSource(): DataSource = configRepository.getDataSource()

    fun switchDataSource(newSource: DataSource) {
        configRepository.setDataSource(newSource)
    }
}
