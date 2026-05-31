package io.github.javiewer.fragment.favourite

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.fragment.RecyclerFragment
import androidx.recyclerview.widget.LinearLayoutManager

abstract class FavouriteFragment<T : Parcelable> : RecyclerFragment<T, LinearLayoutManager>() {

    fun update() {
        getAdapter()?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        setAdapter(adapter())
        mRefreshLayout.isEnabled = false
        decoration()?.let { mRecyclerView.addItemDecoration(it) }
    }

    abstract fun adapter(): ItemAdapter<*, *>

    open fun decoration(): RecyclerView.ItemDecoration? = null
}
