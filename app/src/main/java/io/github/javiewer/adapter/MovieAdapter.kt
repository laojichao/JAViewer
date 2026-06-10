package io.github.javiewer.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.javiewer.R
import io.github.javiewer.activity.MovieActivity
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.databinding.CardMovieBinding

/**
 * 影片列表适配器，用于 RecyclerView 展示影片卡片。
 *
 * 点击卡片跳转到 [MovieActivity] 影片详情页。
 * 使用 Glide 加载封面图片。
 *
 * @property showIfHot 是否显示热门标记，默认 true
 */
open class MovieAdapter(
    items: MutableList<Movie>,
    private val activity: Activity?
) : ItemAdapter<Movie, MovieAdapter.ViewHolder>(items) {

    var showIfHot: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItems()[position]
        holder.bind(movie)
        holder.binding.cardMovie.setOnClickListener {
            val act = activity ?: return@setOnClickListener
            val intent = Intent(act, MovieActivity::class.java)
            intent.putExtra("movie", movie)
            act.startActivity(intent)
        }
        holder.binding.movieCover.setImageDrawable(null)
        Glide.with(holder.binding.movieCover)
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
