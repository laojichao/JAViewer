package io.github.javiewer.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import io.github.javiewer.adapter.MovieAdapter
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.network.provider.AVMOProvider
import io.github.javiewer.view.decoration.MovieItemDecoration
import io.github.javiewer.view.listener.EndlessOnScrollListener
import okhttp3.ResponseBody
import retrofit2.Call

abstract class MovieFragment : RecyclerFragment<Movie, LinearLayoutManager>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setLayoutManager(LinearLayoutManager(context))
        mRecyclerView.addItemDecoration(MovieItemDecoration())
        setAdapter(SlideInBottomAnimationAdapter(MovieAdapter(getItems(), activity)))
        val animator = SlideInUpAnimator()
        animator.addDuration = 300
        mRecyclerView.itemAnimator = animator

        setOnRefreshListener { getOnScrollListener()?.refresh() }

        addOnScrollListener(object : EndlessOnScrollListener<Movie>() {
            override fun newCall(page: Int): Call<ResponseBody>? = this@MovieFragment.newCall(page)
            override fun getLayoutManager() = this@MovieFragment.getLayoutManager()
            override fun getRefreshLayout() = this@MovieFragment.mRefreshLayout
            override fun getItems() = this@MovieFragment.getItems()
            override fun getAdapter() = this@MovieFragment.getAdapter()

            override fun onResult(response: ResponseBody) {
                super.onResult(response)
                val wrappers = AVMOProvider.parseMovies(response.string())
                val pos = getItems().size
                getItems().addAll(wrappers)
                getAdapter()?.notifyItemRangeInserted(pos, wrappers.size())
            }
        })

        mRefreshLayout.post {
            mRefreshLayout.isRefreshing = true
            getOnRefreshListener()?.onRefresh()
        }

        super.onActivityCreated(savedInstanceState)
    }

    abstract fun newCall(page: Int): Call<ResponseBody>?
}
