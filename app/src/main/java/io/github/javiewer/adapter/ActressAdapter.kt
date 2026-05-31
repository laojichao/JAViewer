package io.github.javiewer.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.github.javiewer.R
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.databinding.LayoutActressBinding
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.view.listener.ActressClickListener
import io.github.javiewer.view.listener.ActressLongClickListener

class ActressAdapter(
    items: MutableList<Actress>,
    private val activity: Activity?,
    private val configRepository: ConfigRepository
) : ItemAdapter<Actress, ActressAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutActressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actress = getItems()[position]
        holder.bind(actress)
        holder.binding.layoutActress.setOnClickListener(ActressClickListener(actress, activity))
        holder.binding.layoutActress.setOnLongClickListener(ActressLongClickListener(actress, activity, configRepository))
        holder.binding.actressImg.setImageDrawable(null)
        Glide.with(holder.binding.actressImg.context.applicationContext)
            .load(actress.imageUrl)
            .placeholder(R.drawable.ic_movie_actresses)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .transform(CircleCrop())
            .dontAnimate()
            .into(holder.binding.actressImg)
    }

    class ViewHolder(val binding: LayoutActressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actress: Actress) {
            binding.actressName.text = actress.name
            binding.actressName.isSelected = true
        }
    }
}
