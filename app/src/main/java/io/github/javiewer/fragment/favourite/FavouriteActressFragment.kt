package io.github.javiewer.fragment.favourite

import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.adapter.ActressAdapter
import io.github.javiewer.adapter.ItemAdapter
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.view.decoration.ActressItemDecoration
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 收藏女优列表 Fragment，展示用户收藏的女优。
 *
 * 使用 Hilt 注入 [ConfigRepository] 获取收藏数据。
 */
@AndroidEntryPoint
class FavouriteActressFragment : FavouriteFragment<Actress>() {

    @Inject lateinit var configRepository: ConfigRepository

    override suspend fun loadItems(): MutableList<Actress> {
        return ArrayList(configRepository.getStarredActressesFlow().first())
    }

    override fun createAdapter(items: MutableList<Actress>): ItemAdapter<Actress, *> {
        return ActressAdapter(items, activity, configRepository)
    }

    override fun decoration(): RecyclerView.ItemDecoration = ActressItemDecoration()
}
