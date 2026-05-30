package io.github.javiewer.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.javiewer.JAViewer
import io.github.javiewer.adapter.ActressAdapter
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.network.provider.AVMOProvider
import io.github.javiewer.view.decoration.ActressItemDecoration
import io.github.javiewer.view.listener.EndlessOnScrollListener
import okhttp3.ResponseBody
import retrofit2.Call

class ActressesFragment : RecyclerFragment<Actress, LinearLayoutManager>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        setAdapter(ActressAdapter(getItems(), activity))
        mRecyclerView.addItemDecoration(ActressItemDecoration())

        setOnRefreshListener { getOnScrollListener()?.refresh() }

        addOnScrollListener(object : EndlessOnScrollListener<Actress>() {
            override fun newCall(page: Int): Call<ResponseBody>? = JAViewer.SERVICE?.getActresses(page)
            override fun getLayoutManager() = this@ActressesFragment.getLayoutManager()
            override fun getRefreshLayout() = this@ActressesFragment.mRefreshLayout
            override fun getItems() = this@ActressesFragment.getItems()
            override fun getAdapter() = this@ActressesFragment.getAdapter()

            override fun onResult(response: ResponseBody) {
                super.onResult(response)
                val wrappers = AVMOProvider.parseActresses(response.string())
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
}
