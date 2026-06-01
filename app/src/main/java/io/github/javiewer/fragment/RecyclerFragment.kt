package io.github.javiewer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.javiewer.R
import io.github.javiewer.databinding.FragmentRecyclerBinding
import io.github.javiewer.view.ViewUtil
import io.github.javiewer.view.listener.BasicOnScrollListener

/**
 * 带下拉刷新和分页加载的 RecyclerView Fragment 基类。
 *
 * 封装了 SwipeRefreshLayout + RecyclerView 的初始化、状态保存/恢复、
 * 滚动监听器管理等通用逻辑。子类只需配置 LayoutManager、Adapter 和数据加载逻辑。
 *
 * @param I 列表项数据类型
 * @param @param LM LayoutManager 类型
 */
abstract class RecyclerFragment<I, LM : RecyclerView.LayoutManager> : Fragment() {

    private var _binding: FragmentRecyclerBinding? = null
    protected val binding get() = _binding!!
    protected val mRecyclerView get() = binding.recyclerView
    protected val mRefreshLayout get() = binding.refreshLayout

    private var mRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
    private var mScrollListener: BasicOnScrollListener<I>? = null
    private val items = ArrayList<I>()

    /**
     * 设置 RecyclerView 的内边距。
     *
     * @param dp dp 值
     */
    protected fun setRecyclerViewPadding(dp: Int) {
        val px = ViewUtil.dpToPx(dp)
        mRecyclerView.setPadding(px, px, px, px)
    }

    /** 获取当前 LayoutManager */
    @Suppress("UNCHECKED_CAST")
    fun getLayoutManager(): LM = mRecyclerView.layoutManager as LM

    /** 设置 LayoutManager */
    fun setLayoutManager(manager: LM) {
        mRecyclerView.layoutManager = manager
    }

    /** 获取当前 Adapter */
    fun getAdapter(): RecyclerView.Adapter<*>? = mRecyclerView.adapter

    /** 设置 Adapter */
    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView.adapter = adapter
    }

    /** 获取列表数据源 */
    fun getItems(): ArrayList<I> = items

    /**
     * 替换列表数据并通知刷新。
     *
     * @param @param newItems 新的数据列表
     */
    fun setItems(newItems: ArrayList<I>) {
        items.clear()
        items.addAll(newItems)
        getAdapter()?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.googleBlue),
            ContextCompat.getColor(requireContext(), R.color.googleGreen),
            ContextCompat.getColor(requireContext(), R.color.googleRed),
            ContextCompat.getColor(requireContext(), R.color.googleYellow)
        )
        if (savedInstanceState != null) {
            @Suppress("DEPRECATION")
            getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("LayoutManagerState"))
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            val items = savedInstanceState.getParcelableArrayList<android.os.Parcelable>("Items") as? ArrayList<I>
            if (items != null) setItems(items)
            getOnScrollListener()?.restoreState(savedInstanceState.getBundle("ScrollListenerState") ?: Bundle())
        }
    }

    /**
     * 添加滚动分页监听器。
     *
     * @param listener 分页监听器实例
     */
    fun addOnScrollListener(listener: BasicOnScrollListener<I>) {
        mRecyclerView.addOnScrollListener(listener)
        mScrollListener = listener
    }

    /** 获取当前滚动监听器 */
    fun getOnScrollListener(): BasicOnScrollListener<I>? = mScrollListener

    /** 获取当前刷新监听器 */
    fun getOnRefreshListener(): SwipeRefreshLayout.OnRefreshListener? = mRefreshListener

    /**
     * 设置下拉刷新监听器。
     *
     * @param listener 刷新监听器
     */
    fun setOnRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener) {
        mRefreshLayout.setOnRefreshListener(listener)
        mRefreshListener = listener
    }

    override fun onSaveInstanceState(outState: Bundle) {
        @Suppress("UNCHECKED_CAST")
        outState.putParcelableArrayList("Items", getItems() as ArrayList<android.os.Parcelable>)
        outState.putParcelable("LayoutManagerState", getLayoutManager().onSaveInstanceState())
        getOnScrollListener()?.let { outState.putBundle("ScrollListenerState", it.saveState()) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        mScrollListener?.onViewDestroyed()
        super.onDestroyView()
        _binding = null
    }
}
