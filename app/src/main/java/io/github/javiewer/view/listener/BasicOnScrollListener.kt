package io.github.javiewer.view.listener

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

/**
 * 基于协程的 RecyclerView 滚动分页监听器基类。
 *
 * 实现了完整的分页生命周期：加载中、加载完成、到底结束、取消。
 * 使用 token 机制防止过期请求覆盖新数据。
 *
 * 子类需实现 [getLayoutManager]、[getRefreshLayout]、[getItems]、[getAdapter]、[loadData] 五个抽象方法。
 *
 * @param I 列表项数据类型
 * @param scope 协程作用域，通常为 Fragment 的 `viewLifecycleOwner.lifecycleScope`
 */
abstract class BasicOnScrollListener<I>(
    private val scope: CoroutineScope
) : RecyclerView.OnScrollListener() {

    private var loading = false
    private var loadThreshold = 5
    private var currentPage = 0
    private var token = 0L
    private var end = false
    private var cancelled = false
    private var loadJob: Job? = null

    /** 视图销毁时调用，取消进行中的网络请求 */
    fun onViewDestroyed() {
        cancelled = true
        loadJob?.cancel()
    }

    /** 重置分页状态，清空列表数据 */
    fun reset() {
        loading = false
        loadThreshold = 5
        currentPage = 0
        end = false
        loadJob?.cancel()
        val oldSize = getItems().size
        if (oldSize > 0) {
            getItems().clear()
            getAdapter()?.notifyItemRangeRemoved(0, oldSize)
        }
    }

    /** 保存当前页码到 Bundle */
    fun saveState(): Bundle = Bundle().apply { putInt("CurrentPage", currentPage) }

    /** 从 Bundle 恢复页码 */
    fun restoreState(bundle: Bundle) {
        currentPage = bundle.getInt("CurrentPage")
    }

    /** 获取 RecyclerView 的 LayoutManager */
    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    /** 获取 SwipeRefreshLayout 实例 */
    abstract fun getRefreshLayout(): SwipeRefreshLayout

    /** 获取列表数据源 */
    abstract fun getItems(): MutableList<I>

    /** 获取 RecyclerView 的 Adapter */
    abstract fun getAdapter(): RecyclerView.Adapter<*>?

    /**
     * 加载指定页数据（挂起函数）。
     *
     * @param page 要加载的页码
     * @return 页面 HTML 内容，不支持时返回 null
     */
    abstract suspend fun loadData(page: Int): ResponseBody?

    /**
     * 触发刷新，重置状态并加载第一页。
     */
    fun refresh() {
        setLoading(true)
        reset()
        onLoad(System.currentTimeMillis().also { token = it })
    }

    /** 执行分页加载 */
    private fun onLoad(requestToken: Long) {
        val page = currentPage
        loadJob = scope.launch {
            try {
                val body = loadData(page + 1)
                if (cancelled) return@launch
                if (requestToken == token && page == currentPage) {
                    if (body != null) {
                        body.use { onResult(it) }
                        currentPage++
                    }
                    setLoading(false)
                    getRefreshLayout().isRefreshing = false
                }
            } catch (e: Throwable) {
                if (cancelled) return@launch
                if (requestToken == token && page == currentPage) {
                    setLoading(false)
                    getRefreshLayout().isRefreshing = false
                    onExceptionCaught(e)
                }
            }
        }
    }

    /** 加载异常回调，子类可覆盖 */
    open fun onExceptionCaught(t: Throwable) {}

    /** 加载结果回调，子类覆盖以解析数据 */
    open fun onResult(response: ResponseBody) {}

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!isLoading && !isEnd() && canLoadMore(recyclerView)) {
            isLoading = true
            onLoad(System.currentTimeMillis().also { token = it })
        }
    }

    /**
     * 判断是否可以加载更多数据。
     * 当剩余可见项少于 [loadThreshold] 时触发。
     */
    fun canLoadMore(recyclerView: RecyclerView): Boolean {
        val lm = getLayoutManager()
        val visibleCount = recyclerView.childCount
        val totalCount = lm.itemCount
        val firstVisible = when (lm) {
            is StaggeredGridLayoutManager -> lm.findFirstVisibleItemPositions(null)[0]
            is GridLayoutManager -> lm.findFirstVisibleItemPosition()
            is LinearLayoutManager -> lm.findFirstVisibleItemPosition()
            else -> 0
        }
        return (totalCount - visibleCount) <= (firstVisible + loadThreshold)
    }

    /** 是否正在加载 */
    var isLoading: Boolean
        get() = loading
        set(value) { loading = value }

    /** 设置加载状态 */
    @JvmName("setLoadingFlag")
    fun setLoading(value: Boolean) { loading = value }

    /** 是否已到底（无更多数据） */
    open fun isEnd(): Boolean = end

    /** 设置到底状态 */
    fun setEnd(value: Boolean) { end = value }
}
