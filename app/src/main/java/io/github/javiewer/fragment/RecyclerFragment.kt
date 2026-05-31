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

abstract class RecyclerFragment<I, LM : RecyclerView.LayoutManager> : Fragment() {

    private var _binding: FragmentRecyclerBinding? = null
    protected val binding get() = _binding!!
    protected val mRecyclerView get() = binding.recyclerView
    protected val mRefreshLayout get() = binding.refreshLayout

    private var mRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
    private var mScrollListener: BasicOnScrollListener<I>? = null
    private val items = ArrayList<I>()

    protected fun setRecyclerViewPadding(dp: Int) {
        val px = ViewUtil.dpToPx(dp)
        mRecyclerView.setPadding(px, px, px, px)
    }

    @Suppress("UNCHECKED_CAST")
    fun getLayoutManager(): LM = mRecyclerView.layoutManager as LM

    fun setLayoutManager(manager: LM) {
        mRecyclerView.layoutManager = manager
    }

    fun getAdapter(): RecyclerView.Adapter<*>? = mRecyclerView.adapter

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView.adapter = adapter
    }

    fun getItems(): ArrayList<I> = items

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

    fun addOnScrollListener(listener: BasicOnScrollListener<I>) {
        mRecyclerView.addOnScrollListener(listener)
        mScrollListener = listener
    }

    fun getOnScrollListener(): BasicOnScrollListener<I>? = mScrollListener

    fun getOnRefreshListener(): SwipeRefreshLayout.OnRefreshListener? = mRefreshListener

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
