package io.github.javiewer.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.javiewer.R
import io.github.javiewer.activity.MovieActivity
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.databinding.CardMovieBinding

open class MovieAdapter(
    items: MutableList<Movie>,
    private val activity: Activity?
) : ItemAdapter<Movie, MovieAdapter.ViewHolder>(items) {

    var showIfHot: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItems()[position]
        holder.bind(movie)
        holder.binding.cardMovie.setOnClickListener {
            val intent = Intent(activity, MovieActivity::class.java)
            intent.putExtra("movie", movie)
            activity?.startActivity(intent)
        }
        holder.binding.movieCover.setImageDrawable(null)
        Glide.with(holder.binding.movieCover.context.applicationContext)
            .load(movie.coverUrl)
            .into(holder.binding.movieCover)
        holder.binding.movieHot.visibility = if (movie.hot && showIfHot) View.VISIBLE else View.GONE
    }

    class ViewHolder(val binding: CardMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieSize.text = movie.code
            binding.movieTitle.text = movie.title
            binding.movieDate.text = movie.date
        }
    }
}
