package io.github.javiewer.fragment.genre

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.javiewer.adapter.GenreAdapter
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.fragment.RecyclerFragment
import io.github.javiewer.view.decoration.GridSpacingItemDecoration

/**
 * 类别列表 Fragment，以网格形式展示单个类别组下的所有类别标签。
 *
 * 由 [GenreTabsFragment] 创建并传入类别数据。下拉刷新功能禁用。
 */
class GenreFragment : RecyclerFragment<Genre, LinearLayoutManager>() {

    private val genres = mutableListOf<Genre>()

    /** 获取类别数据列表，供外部添加数据 */
    fun getGenres(): MutableList<Genre> = genres

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        setAdapter(GenreAdapter(genres, activity))
        mRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        mRefreshLayout.isEnabled = false
    }
}
