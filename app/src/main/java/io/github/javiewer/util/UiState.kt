package io.github.javiewer.util

/**
 * 通用 UI 状态密封类，用于 ViewModel 向 UI 层传递加载状态。
 *
 * 使用 [Loading]、[Success]、[Error] 三种状态表示异步操作的完整生命周期，
 * 配合 Compose 或 LiveData/StateFlow 使用。
 *
 * @param T 成功时承载的数据类型
 */
sealed class UiState<out T> {
    /** 加载中状态 */
    data object Loading : UiState<Nothing>()

    /**
     * 加载成功状态
     *
     * @param data 成功获取的数据
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * 加载失败状态
     *
     * @property message 错误描述信息
     */
    data class Error(val message: String) : UiState<Nothing>()
}
