package io.github.javiewer.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter2
import io.github.javiewer.databinding.ActivityFavouriteBinding
import io.github.javiewer.fragment.favourite.FavouriteActressFragment
import io.github.javiewer.fragment.favourite.FavouriteFragment
import io.github.javiewer.fragment.favourite.FavouriteMovieFragment
import java.lang.ref.WeakReference

/**
 * 收藏夹 Activity，使用底部导航栏切换"作品"和"女优"两个标签页。
 *
 * 通过静态 [WeakReference] 持有实例，允许从 [MovieActivity] 触发列表刷新。
 * **注意**：此 WeakReference 模式是反模式，后续应使用 SharedFlow 或共享 ViewModel 替代。
 */
@AndroidEntryPoint
class FavouriteActivity : SecureActivity() {

    companion object {
        private var sInstance: WeakReference<FavouriteActivity>? = null

        /**
         * 通知收藏夹刷新数据。从其他 Activity（如 MovieActivity）调用。
         */
        @JvmStatic
        fun update() {
            val activity = sInstance?.get() ?: return
            // 通过 FragmentManager 查找已创建的 FavouriteFragment 实例
            for (fragment in activity.supportFragmentManager.fragments) {
                (fragment as? FavouriteFragment<*>)?.update()
            }
        }
    }

    private lateinit var binding: ActivityFavouriteBinding
    var mAdapter: ViewPagerAdapter2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sInstance = WeakReference(this)

        setSupportActionBar(binding.toolbarFav)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAdapter = ViewPagerAdapter2(this)
        mAdapter?.addFragment(FavouriteMovieFragment(), "作品")
        mAdapter?.addFragment(FavouriteActressFragment(), "女优")

        binding.favouriteViewPager.adapter = mAdapter

        // BottomNavigationView 与 ViewPager2 联动
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fav_movie -> {
                    binding.favouriteViewPager.currentItem = 0
                    true
                }
                R.id.nav_fav_actresses -> {
                    binding.favouriteViewPager.currentItem = 1
                    true
                }
                else -> false
            }
        }

        binding.favouriteViewPager.registerOnPageChangeCallback(
            object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNavigation.menu.getItem(position).isChecked = true
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter = null
        if (sInstance?.get() === this) sInstance = null
    }
}
