package io.github.javiewer.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import io.github.javiewer.adapter.DownloadLinkAdapter
import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.network.provider.DownloadLinkProvider
import io.github.javiewer.view.decoration.DownloadItemDecoration
import io.github.javiewer.view.listener.BasicOnScrollListener
import okhttp3.ResponseBody
import retrofit2.Call

class DownloadFragment : RecyclerFragment<DownloadLink, LinearLayoutManager>() {

    private var provider: DownloadLinkProvider? = null
    private var keyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        provider = DownloadLinkProvider.getProvider(bundle?.getString("provider") ?: "")
        keyword = bundle?.getString("keyword") ?: ""
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setLayoutManager(LinearLayoutManager(context))
        setAdapter(ScaleInAnimationAdapter(DownloadLinkAdapter(getItems(), activity, provider!!)))
        mRecyclerView.addItemDecoration(DownloadItemDecoration())

        val animator = SlideInUpAnimator()
        animator.addDuration = 300
        mRecyclerView.itemAnimator = animator

        setOnRefreshListener { getOnScrollListener()?.refresh() }

        addOnScrollListener(object : BasicOnScrollListener<DownloadLink>() {
            override fun newCall(page: Int): Call<ResponseBody>? = provider?.search(keyword, page)
            override fun getLayoutManager() = this@DownloadFragment.getLayoutManager()
            override fun getRefreshLayout() = this@DownloadFragment.mRefreshLayout
            override fun getItems() = this@DownloadFragment.getItems()
            override fun getAdapter() = this@DownloadFragment.getAdapter()

            override fun onResult(response: ResponseBody) {
                super.onResult(response)
                val downloads = provider!!.parseDownloadLinks(response.string())
                val pos = getItems().size
                if (downloads.isEmpty()) {
                    setEnd(true)
                } else {
                    getItems().addAll(downloads)
                    getAdapter()?.notifyItemRangeInserted(pos, downloads.size)
                }
            }
        })

        mRefreshLayout.post {
            mRefreshLayout.isRefreshing = true
            getOnRefreshListener()?.onRefresh()
        }

        super.onActivityCreated(savedInstanceState)
    }
}
