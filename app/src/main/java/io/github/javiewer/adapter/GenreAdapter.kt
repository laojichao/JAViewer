package io.github.javiewer.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.activity.MovieListActivity
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.databinding.CardGenreBinding

class GenreAdapter(
    private val genres: List<Genre>,
    private val activity: Activity?
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genres[position]
        holder.binding.genreName.text = genre.name
        holder.binding.cardGenre.setOnClickListener {
            if (genre.link != null) {
                activity?.startActivity(MovieListActivity.newIntent(activity, genre.name, genre.link!!))
            }
        }
    }

    override fun getItemCount(): Int = genres.size

    class ViewHolder(val binding: CardGenreBinding) : RecyclerView.ViewHolder(binding.root)
}
