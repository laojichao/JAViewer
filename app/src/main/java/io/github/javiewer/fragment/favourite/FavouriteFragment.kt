package io.github.javiewer.fragment.favourite

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.fragment.RecyclerFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 收藏列表 Fragment 抽象基类，用于展示收藏的影片或女优。
 *
 * 下拉刷新功能禁用。子类需实现 [loadItems] 提供数据加载逻辑。
 *
 * @param T 收藏项数据类型，必须实现 [Parcelable]
 */
abstract class FavouriteFragment<T : Parcelable> : RecyclerFragment<T, LinearLayoutManager>() {

    /** 重新从数据库加载收藏数据并刷新列表 */
    fun update() {
        if (!isAdded) return
        viewLifecycleOwner.lifecycleScope.launch {
            val newItems = withContext(Dispatchers.IO) { loadItems() }
            if (isAdded) {
                val adapter = getAdapter()
                if (adapter is ItemAdapter<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    (adapter as ItemAdapter<T, *>).setItems(newItems)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        mRefreshLayout.isEnabled = false
        decoration()?.let { mRecyclerView.addItemDecoration(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            val items = loadItems()
            if (isAdded) {
                val adapter = createAdapter(items)
                setAdapter(adapter)
            }
        }
    }

    /**
     * 异步加载收藏数据，由子类实现。
     *
     * @return 收藏数据列表
     */
    abstract suspend fun loadItems(): MutableList<T>

    /**
     * 创建列表适配器，由子类实现。
     *
     * @param items 数据列表
     * @return 适配器实例
     */
    abstract fun createAdapter(items: MutableList<T>): ItemAdapter<T, *>

    /** 可选的列表项装饰 */
    open fun decoration(): RecyclerView.ItemDecoration? = null
}
