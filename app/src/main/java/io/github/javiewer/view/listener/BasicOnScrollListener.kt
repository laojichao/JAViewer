package io.github.javiewer.view.listener

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BasicOnScrollListener<I> : RecyclerView.OnScrollListener() {

    private var loading = false
    private var loadThreshold = 5
    private var currentPage = 0
    private var token = 0L
    private var end = false

    fun reset() {
        loading = false
        loadThreshold = 5
        currentPage = 0
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
    abstract fun newCall(page: Int): Call<ResponseBody>?

    fun refresh() {
        setLoading(true)
        reset()
        onLoad(System.currentTimeMillis().also { token = it })
    }

    private fun onLoad(t: Long) {
        val page = currentPage
        val call = newCall(page + 1) ?: run {
            setLoading(false)
            getRefreshLayout().isRefreshing = false
            return
        }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (t == token && page == currentPage) {
                    try {
                        val body = response.body()
                        if (body != null) {
                            onResult(body)
                            currentPage++
                        }
                    } catch (e: Throwable) {
                        onFailure(call, e)
                    }
                }
                setLoading(false)
                getRefreshLayout().isRefreshing = false
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                setLoading(false)
                getRefreshLayout().isRefreshing = false
                onExceptionCaught(t)
            }
        })
    }

    open fun onExceptionCaught(t: Throwable) {}
    open fun onResult(response: ResponseBody) {}

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!isLoading && canLoadMore(recyclerView)) {
            onLoad(System.currentTimeMillis().also { token = it })
            loading = true
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

    fun setLoading(value: Boolean) { loading = value }

    fun isEnd(): Boolean = end
    fun setEnd(value: Boolean) { end = value }
}
