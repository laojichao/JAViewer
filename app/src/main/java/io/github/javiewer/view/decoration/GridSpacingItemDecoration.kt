package io.github.javiewer.view.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.view.ViewUtil

/**
 * 网格布局间距装饰，支持均匀分布间距和边缘间距。
 *
 * @property spanCount 网格列数
 * @property spacingDp 间距值（dp）
 * @property includeEdge 是否包含边缘间距
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingDp: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return
        val column = position % spanCount
        val spacing = ViewUtil.dpToPx(spacingDp)

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) outRect.top = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) outRect.top = spacing
        }
    }
}
