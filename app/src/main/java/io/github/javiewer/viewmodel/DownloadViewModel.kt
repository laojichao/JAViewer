package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.repository.ConfigRepository
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    fun getDownloadCounter(): Long = configRepository.getDownloadCounter()

    fun setDownloadCounter(counter: Long) {
        configRepository.setDownloadCounter(counter)
    }
}
