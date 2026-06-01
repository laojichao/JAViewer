package io.github.javiewer.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.javiewer.adapter.DownloadLinkAdapter
import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.network.provider.DownloadLinkProvider
import io.github.javiewer.view.decoration.DownloadItemDecoration
import io.github.javiewer.view.listener.BasicOnScrollListener
import okhttp3.ResponseBody

/**
 * 下载链接列表 Fragment，展示指定种子搜索站点的搜索结果。
 *
 * 通过 arguments 的 "provider" 字段指定搜索站点名称（btso/torrentkitty/bh/btmovi），
 * "keyword" 字段指定搜索关键词。使用有限分页（非无限滚动）。
 */
class DownloadFragment : RecyclerFragment<DownloadLink, LinearLayoutManager>() {

    private var provider: DownloadLinkProvider? = null
    private var keyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        provider = DownloadLinkProvider.getProvider(bundle?.getString("provider") ?: "")
        keyword = bundle?.getString("keyword") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        val p = provider ?: return
        setAdapter(DownloadLinkAdapter(getItems(), activity, p))
        mRecyclerView.addItemDecoration(DownloadItemDecoration())

        setOnRefreshListener { getOnScrollListener()?.refresh() }

        addOnScrollListener(object : BasicOnScrollListener<DownloadLink>(viewLifecycleOwner.lifecycleScope) {
            override suspend fun loadData(page: Int): ResponseBody? = provider?.search(keyword, page)
            override fun getLayoutManager() = this@DownloadFragment.getLayoutManager()
            override fun getRefreshLayout() = this@DownloadFragment.mRefreshLayout
            override fun getItems() = this@DownloadFragment.getItems()
            override fun getAdapter() = this@DownloadFragment.getAdapter()

            override fun onResult(response: ResponseBody) {
                super.onResult(response)
                val downloads = provider?.parseDownloadLinks(response.string()) ?: return
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

    }
}
