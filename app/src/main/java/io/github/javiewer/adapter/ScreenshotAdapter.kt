package io.github.javiewer.adapter

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.javiewer.activity.GalleryActivity
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.Screenshot
import io.github.javiewer.databinding.LayoutScreenshotBinding
import io.github.javiewer.view.ViewUtil

class ScreenshotAdapter(
    private val screenshots: List<Screenshot>,
    private val activity: Activity?,
    private val icon: ImageView,
    private val movie: Movie
) : RecyclerView.Adapter<ScreenshotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutScreenshotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val screenshot = screenshots[position]
        holder.binding.screenshotImageView.setImageDrawable(null)
        Glide.with(holder.binding.screenshotImageView.context.applicationContext)
            .load(screenshot.thumbnailUrl)
            .into(holder.binding.screenshotImageView)

        holder.binding.screenshotImageView.setOnClickListener {
            val urls = screenshots.map { it.imageUrl ?: "" }.toTypedArray()
            val bundle = Bundle().apply {
                putStringArray("urls", urls)
                putParcelable("movie", movie)
                putInt("position", holder.adapterPosition)
            }
            activity?.startActivity(android.content.Intent(activity, GalleryActivity::class.java).putExtras(bundle))
        }
        if (position == 0) ViewUtil.alignIconToView(icon, holder.binding.screenshotImageView)
    }

    override fun getItemCount(): Int = screenshots.size

    class ViewHolder(val binding: LayoutScreenshotBinding) : RecyclerView.ViewHolder(binding.root)
}
