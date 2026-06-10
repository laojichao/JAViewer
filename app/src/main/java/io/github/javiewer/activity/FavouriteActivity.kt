package io.github.javiewer.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter
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
            val activity = sInstance?.get()
            if (activity != null && activity.mAdapter != null) {
                for (i in 0 until activity.mAdapter!!.count) {
                    (activity.mAdapter!!.getItem(i) as? FavouriteFragment<*>)?.update()
                }
            }
        }
    }

    private lateinit var binding: ActivityFavouriteBinding
    var mAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sInstance = WeakReference(this)

        setSupportActionBar(binding.toolbarFav)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAdapter = ViewPagerAdapter(supportFragmentManager)
        binding.favouriteViewPager.adapter = mAdapter
        binding.favouriteViewPager.setPagingEnabled(true)
        binding.favouriteViewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.setCurrentItem(position)
                binding.bottomNavigation.restoreBottomNavigation()
            }
        })

        mAdapter?.addFragment(FavouriteMovieFragment(), "作品")
        mAdapter?.addFragment(FavouriteActressFragment(), "女优")
        mAdapter?.notifyDataSetChanged()

        val navigationAdapter = AHBottomNavigationAdapter(this, R.menu.nav_favourite)
        navigationAdapter.setupWithBottomNavigation(binding.bottomNavigation)
        binding.bottomNavigation.setTranslucentNavigationEnabled(true)
        binding.bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary))
        binding.bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        binding.bottomNavigation.setOnTabSelectedListener { position, wasSelected ->
            if (!wasSelected) {
                binding.favouriteViewPager.currentItem = position
                true
            } else false
        }
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
