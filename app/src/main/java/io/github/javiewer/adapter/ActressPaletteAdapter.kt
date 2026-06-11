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
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.view.SquareTopCrop
import io.github.javiewer.view.ViewUtil
import io.github.javiewer.view.listener.ActressClickListener
import io.github.javiewer.view.listener.ActressLongClickListener

/**
 * 女优调色板适配器，用于影片详情页展示女优头像卡片。
 *
 * 使用 Palette 库从头像图片中提取亮色调作为卡片背景色，
 * 实现视觉上的动态配色效果。
 *
 * @param actresses 女优数据列表
 * @param activity 当前 Activity
 * @param icon 需要对齐的图标视图
 * @param configRepository 用户配置仓库，用于收藏操作
 */
class ActressPaletteAdapter(
    private val actresses: List<Actress>,
    private val activity: Activity?,
    private val icon: ImageView,
    private val configRepository: ConfigRepository
) : RecyclerView.Adapter<ActressPaletteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardActressPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        val actress = actresses[pos]
        holder.binding.cardActressPalette.setOnClickListener(ActressClickListener(actress, activity))
        holder.binding.cardActressPalette.setOnLongClickListener(ActressLongClickListener(actress, activity, configRepository))
        holder.binding.actressPaletteName.text = actress.name
        holder.binding.cardActressPalette.setCardBackgroundColor(0)
        if (pos == 0) ViewUtil.alignIconToView(icon, holder.binding.actressPaletteImg)
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
                    if (holder.bindingAdapterPosition != pos) return
                    holder.binding.actressPaletteImg.setImageBitmap(resource)
                    try {
                        Palette.from(resource).generate { palette ->
                            if (holder.bindingAdapterPosition != pos) return@generate
                            val swatch = palette?.lightVibrantSwatch ?: return@generate
                            holder.binding.cardActressPalette.setCardBackgroundColor(swatch.rgb)
                            holder.binding.actressPaletteName.setTextColor(swatch.bodyTextColor)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ActressPaletteAdapter", "Palette generation failed", e)
                    }
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
            })
    }

    override fun getItemCount(): Int = actresses.size

    class ViewHolder(val binding: CardActressPaletteBinding) : RecyclerView.ViewHolder(binding.root)
}
