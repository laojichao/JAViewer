package io.github.javiewer.adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.R
import io.github.javiewer.activity.MovieListActivity
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.databinding.LayoutHeaderBinding
import io.github.javiewer.view.ViewUtil

class MovieHeaderAdapter(
    private val headers: List<MovieDetail.Header>,
    private val activity: Activity?,
    private val icon: ImageView
) : RecyclerView.Adapter<MovieHeaderAdapter.ViewHolder>() {

    private var first = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val header = headers[position]
        if (header.name != null && header.value != null) {
            holder.itemView.setOnLongClickListener {
                val clip = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                clip?.setPrimaryClip(ClipData.newPlainText(header.name, header.value))
                Toast.makeText(activity, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                true
            }
            holder.binding.headerName.text = header.name
            holder.binding.headerValue.text = header.value

            if (header.link != null) {
                holder.binding.headerValue.paintFlags = holder.binding.headerValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                holder.binding.headerValue.setTextColor(ResourcesCompat.getColor(activity!!.resources, R.color.colorAccent, null))
                holder.binding.headerValue.setOnClickListener {
                    activity.startActivity(MovieListActivity.newIntent(activity, "${header.name} ${header.value}", header.link!!))
                }
            }
            if (first) {
                ViewUtil.alignIconToView(icon, holder.binding.headerName)
                first = false
            }
        }
    }

    override fun getItemCount(): Int = headers.size

    class ViewHolder(val binding: LayoutHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}
