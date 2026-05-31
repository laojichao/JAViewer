package io.github.javiewer.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter
import io.github.javiewer.databinding.ActivityFavouriteBinding
import io.github.javiewer.fragment.favourite.FavouriteActressFragment
import io.github.javiewer.fragment.favourite.FavouriteFragment
import io.github.javiewer.fragment.favourite.FavouriteMovieFragment
import java.lang.ref.WeakReference

@AndroidEntryPoint
class FavouriteActivity : SecureActivity() {

    companion object {
        private var sInstance: WeakReference<FavouriteActivity>? = null

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
            @Suppress("DEPRECATION")
            onBackPressed()
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
