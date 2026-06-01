package io.github.javiewer.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.repository.ConfigRepository
import javax.inject.Inject

/**
 * 主页 ViewModel，管理数据源切换。
 *
 * @property configRepository 用户配置仓库
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    /** 获取当前数据源 */
    fun getDataSource(): DataSource = configRepository.getDataSource()

    /**
     * 切换数据源并持久化。
     *
     * @param newSource 新的数据源
     */
    fun switchDataSource(newSource: DataSource) {
        configRepository.setDataSource(newSource)
    }
}
