package io.github.javiewer.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.javiewer.adapter.MovieAdapter
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.network.provider.AVMOProvider
import io.github.javiewer.view.decoration.MovieItemDecoration
import io.github.javiewer.view.listener.EndlessOnScrollListener
import okhttp3.ResponseBody

/**
 * 影片列表 Fragment 抽象基类，配置 LinearLayoutManager、影片适配器和无限滚动分页。
 *
 * 子类只需实现 [loadData] 提供对应页面的 Retrofit 调用即可。
 * 用于主页、热门、已发布、搜索结果等影片列表场景。
 */
abstract class MovieFragment : RecyclerFragment<Movie, LinearLayoutManager>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        mRecyclerView.addItemDecoration(MovieItemDecoration())
        setAdapter(MovieAdapter(getItems(), activity))

        setOnRefreshListener { getOnScrollListener()?.refresh() }

        addOnScrollListener(object : EndlessOnScrollListener<Movie>(viewLifecycleOwner.lifecycleScope) {
            override suspend fun loadData(page: Int): ResponseBody? = this@MovieFragment.loadData(page)
            override fun getLayoutManager() = this@MovieFragment.getLayoutManager()
            override fun getRefreshLayout() = this@MovieFragment.mRefreshLayout
            override fun getItems() = this@MovieFragment.getItems()
            override fun getAdapter() = this@MovieFragment.getAdapter()

            override fun onResult(response: ResponseBody) {
                super.onResult(response)
                try {
                    val wrappers = AVMOProvider.parseMovies(response.string())
                    val pos = getItems().size
                    getItems().addAll(wrappers)
                    getAdapter()?.notifyItemRangeInserted(pos, wrappers.size)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        mRefreshLayout.post {
            mRefreshLayout.isRefreshing = true
            getOnRefreshListener()?.onRefresh()
        }

    }

    /**
     * 加载指定页数据，由子类实现。
     *
     * @param page 页码
     * @return 页面 HTML 内容
     */
    abstract suspend fun loadData(page: Int): ResponseBody?
}
