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

/**
 * 截图列表适配器，用于影片详情页展示截图网格。
 *
 * 点击截图跳转到 [GalleryActivity] 图片画廊，支持缩放浏览。
 * 首项自动对齐图标位置。
 *
 * @param screenshots 截图数据列表
 * @param activity 当前 Activity
 * @param icon 需要对齐的图标视图
 * @param movie 所属影片数据，传递给画廊页
 */
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        val screenshot = screenshots[pos]
        holder.binding.screenshotImageView.setImageDrawable(null)
        Glide.with(holder.binding.screenshotImageView.context.applicationContext)
            .load(screenshot.thumbnailUrl)
            .into(holder.binding.screenshotImageView)

        holder.binding.screenshotImageView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val urls = screenshots.map { it.imageUrl ?: "" }.toTypedArray()
            val bundle = Bundle().apply {
                putStringArray("urls", urls)
                putParcelable("movie", movie)
                putInt("position", pos)
            }
            activity?.startActivity(android.content.Intent(activity, GalleryActivity::class.java).putExtras(bundle))
        }
        if (pos == 0) ViewUtil.alignIconToView(icon, holder.binding.screenshotImageView)
    }

    override fun getItemCount(): Int = screenshots.size

    class ViewHolder(val binding: LayoutScreenshotBinding) : RecyclerView.ViewHolder(binding.root)
}
