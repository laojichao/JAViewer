package io.github.javiewer.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class ItemAdapter<I, VH : RecyclerView.ViewHolder>(private val items: MutableList<I>) : RecyclerView.Adapter<VH>() {

    fun getItems(): MutableList<I> = items

    fun setItems(newItems: List<I>) {
        val size = items.size
        if (size > 0) {
            items.clear()
            notifyItemRangeRemoved(0, size)
        }
        items.addAll(newItems)
        notifyItemRangeInserted(0, newItems.size)
    }

    override fun getItemCount(): Int = items.size

    override fun onViewDetachedFromWindow(holder: VH) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }
}
