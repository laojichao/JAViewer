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
                val wrappers = AVMOProvider.parseMovies(response.string())
                val pos = getItems().size
                getItems().addAll(wrappers)
                getAdapter()?.notifyItemRangeInserted(pos, wrappers.size)
            }
        })

        mRefreshLayout.post {
            mRefreshLayout.isRefreshing = true
            getOnRefreshListener()?.onRefresh()
        }

    }

    abstract suspend fun loadData(page: Int): ResponseBody?
}
