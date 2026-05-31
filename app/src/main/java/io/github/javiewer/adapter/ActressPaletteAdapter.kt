package io.github.javiewer.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.javiewer.R
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.databinding.CardActressPaletteBinding
import io.github.javiewer.view.SquareTopCrop
import io.github.javiewer.view.ViewUtil
import io.github.javiewer.view.listener.ActressClickListener
import io.github.javiewer.view.listener.ActressLongClickListener

class ActressPaletteAdapter(
    private val actresses: List<Actress>,
    private val activity: Activity?,
    private val icon: ImageView
) : RecyclerView.Adapter<ActressPaletteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardActressPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actress = actresses[position]
        holder.binding.cardActressPalette.setOnClickListener(ActressClickListener(actress, activity))
        holder.binding.cardActressPalette.setOnLongClickListener(ActressLongClickListener(actress, activity))
        holder.binding.actressPaletteName.text = actress.name
        holder.binding.cardActressPalette.setCardBackgroundColor(0)
        if (position == 0) ViewUtil.alignIconToView(icon, holder.binding.actressPaletteImg)
        holder.binding.actressPaletteImg.setImageResource(R.drawable.ic_movie_actresses)
        if (actress.imageUrl.trim().isEmpty()) return

        Glide.with(holder.itemView)
            .asBitmap()
            .load(actress.imageUrl)
            .placeholder(R.drawable.ic_movie_actresses)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .transform(SquareTopCrop())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (holder.bindingAdapterPosition != position) return
                    holder.binding.actressPaletteImg.setImageBitmap(resource)
                    try {
                        Palette.from(resource).generate { palette ->
                            if (holder.bindingAdapterPosition != position) return@generate
                            val swatch = palette?.lightVibrantSwatch ?: return@generate
                            holder.binding.cardActressPalette.setCardBackgroundColor(swatch.rgb)
                            holder.binding.actressPaletteName.setTextColor(swatch.bodyTextColor)
                        }
                    } catch (_: Exception) {}
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
            })
    }

    override fun getItemCount(): Int = actresses.size

    class ViewHolder(val binding: CardActressPaletteBinding) : RecyclerView.ViewHolder(binding.root)
}
