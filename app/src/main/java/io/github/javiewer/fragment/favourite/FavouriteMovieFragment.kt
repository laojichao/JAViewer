package io.github.javiewer.fragment.favourite

import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.JAViewer
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.adapter.MovieAdapter
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.view.decoration.MovieItemDecoration

class FavouriteMovieFragment : FavouriteFragment<Movie>() {
    override fun adapter(): ItemAdapter<*, *> {
        return MovieAdapter(JAViewer.CONFIGURATIONS?.getStarredMovies() ?: arrayListOf(), activity).apply {
            showIfHot = false
        }
    }

    override fun decoration(): RecyclerView.ItemDecoration = MovieItemDecoration()
}
