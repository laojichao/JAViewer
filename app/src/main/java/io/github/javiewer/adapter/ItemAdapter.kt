package io.github.javiewer.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView.Adapter 的泛型基类，封装列表数据管理和通知逻辑。
 *
 * @param I 列表项数据类型
 * @param @param VH ViewHolder 类型
 * @property items 列表数据源
 */
abstract class ItemAdapter<I, VH : RecyclerView.ViewHolder>(private val items: MutableList<I>) : RecyclerView.Adapter<VH>() {

    /** 获取列表数据源 */
    fun getItems(): MutableList<I> = items

    /**
     * 替换列表数据，使用 [notifyItemRangeRemoved] + [notifyItemRangeInserted] 通知变更。
     *
     * @param newItems 新的数据列表
     */
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
