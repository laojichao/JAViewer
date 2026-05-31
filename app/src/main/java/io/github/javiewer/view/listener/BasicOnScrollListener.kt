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

    fun onViewDestroyed() {
        cancelled = true
        loadJob?.cancel()
    }

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

    fun saveState(): Bundle = Bundle().apply { putInt("CurrentPage", currentPage) }

    fun restoreState(bundle: Bundle) {
        currentPage = bundle.getInt("CurrentPage")
    }

    abstract fun getLayoutManager(): RecyclerView.LayoutManager
    abstract fun getRefreshLayout(): SwipeRefreshLayout
    abstract fun getItems(): MutableList<I>
    abstract fun getAdapter(): RecyclerView.Adapter<*>?
    abstract suspend fun loadData(page: Int): ResponseBody?

    fun refresh() {
        setLoading(true)
        reset()
        onLoad(System.currentTimeMillis().also { token = it })
    }

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

    open fun onExceptionCaught(t: Throwable) {}
    open fun onResult(response: ResponseBody) {}

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!isLoading && !isEnd() && canLoadMore(recyclerView)) {
            loading = true
            onLoad(System.currentTimeMillis().also { token = it })
        }
    }

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

    var isLoading: Boolean
        get() = loading
        set(value) { loading = value }

    @JvmName("setLoadingFlag")
    fun setLoading(value: Boolean) { loading = value }

    open fun isEnd(): Boolean = end
    fun setEnd(value: Boolean) { end = value }
}
