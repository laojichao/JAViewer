package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.repository.ConfigRepository
import javax.inject.Inject

/**
 * 下载页 ViewModel，管理下载计数器（用于捐赠提示逻辑）。
 *
 * @property configRepository 用户配置仓库
 */
@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    /** 获取当前下载计数 */
    fun getDownloadCounter(): Long = configRepository.getDownloadCounter()

    /**
     * 设置下载计数。
     *
     * @param counter 新的计数值
     */
    fun setDownloadCounter(counter: Long) {
        configRepository.setDownloadCounter(counter)
    }
}
