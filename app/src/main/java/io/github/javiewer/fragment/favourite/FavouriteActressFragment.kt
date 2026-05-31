package io.github.javiewer.fragment.favourite

import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.JAViewer
import io.github.javiewer.adapter.ActressAdapter
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.view.decoration.ActressItemDecoration

class FavouriteActressFragment : FavouriteFragment<Actress>() {
    override fun adapter(): ItemAdapter<*, *> {
        return ActressAdapter(JAViewer.CONFIGURATIONS?.getStarredActresses() ?: arrayListOf(), activity)
    }

    override fun decoration(): RecyclerView.ItemDecoration = ActressItemDecoration()
}
