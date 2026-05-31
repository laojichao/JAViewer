package io.github.javiewer.fragment.favourite

import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.adapter.ActressAdapter
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.view.decoration.ActressItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteActressFragment : FavouriteFragment<Actress>() {

    @Inject lateinit var configRepository: ConfigRepository

    override fun adapter(): ItemAdapter<*, *> {
        return ActressAdapter(configRepository.getStarredActresses(), activity, configRepository)
    }

    override fun decoration(): RecyclerView.ItemDecoration = ActressItemDecoration()
}
