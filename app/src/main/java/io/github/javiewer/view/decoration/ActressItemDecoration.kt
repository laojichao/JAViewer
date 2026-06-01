package io.github.javiewer.view.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.view.ViewUtil

/**
 * 女优列表项装饰，为首项添加 8dp 顶部间距。
 */
class ActressItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val rect = Rect()
        if (parent.getChildAdapterPosition(view) == 0) rect.top = ViewUtil.dpToPx(8)
        outRect.set(rect)
    }
}
