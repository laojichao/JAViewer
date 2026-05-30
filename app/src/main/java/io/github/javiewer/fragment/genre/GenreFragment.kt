package io.github.javiewer.fragment.genre

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.javiewer.adapter.GenreAdapter
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.fragment.RecyclerFragment
import io.github.javiewer.view.decoration.GridSpacingItemDecoration

class GenreFragment : RecyclerFragment<Genre, LinearLayoutManager>() {

    private val genres = mutableListOf<Genre>()

    fun getGenres(): MutableList<Genre> = genres

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager(LinearLayoutManager(context))
        setAdapter(GenreAdapter(genres, activity))
        mRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        mRefreshLayout.isEnabled = false
    }
}
