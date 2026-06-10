package io.github.javiewer.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.adapter.item.DataSource
import io.github.javiewer.databinding.ActivityMainBinding
import io.github.javiewer.fragment.ActressesFragment
import io.github.javiewer.fragment.ExtendedAppBarFragment
import io.github.javiewer.fragment.HomeFragment
import io.github.javiewer.fragment.PopularFragment
import io.github.javiewer.fragment.ReleasedFragment
import io.github.javiewer.fragment.genre.GenreTabsFragment
import io.github.javiewer.network.BasicService
import io.github.javiewer.repository.ConfigRepository
import io.github.javiewer.repository.DataSourceRepository
import io.github.javiewer.view.SimpleSearchView
import java.net.URLEncoder
import javax.inject.Inject

/**
 * 旧版主页 Activity（XML 布局），使用 MaterialDrawer 实现导航抽屉。
 *
 * 管理 5 个 Fragment（主页/已发布/热门/女优/类别）的显示/隐藏切换，
 * 支持搜索、数据源切换和收藏夹跳转。
 *
 * **注意**：此 Activity 正在被 Compose 版 [io.github.javiewer.ui.MainActivity] 逐步替代。
 *
 * @property configRepository 用户配置仓库
 * @property dataSourceRepository 数据源仓库
 */
@AndroidEntryPoint
class MainActivity : SecureActivity() {

    companion object {
        private const val TAG = "MainActivity"
        const val ID_HOME = 1L
        const val ID_FAV = 2L
        const val ID_POPULAR = 3L
        const val ID_RELEASED = 4L
        const val ID_ACTRESSES = 5L
        const val ID_GENRE = 6L
        const val ID_GITHUB1 = 7L
        const val ID_GITHUB2 = 8L
        const val ID_GITHUB3 = 9L

        /** Fragment 类映射表 */
        val FRAGMENTS = mapOf(
            ID_HOME to HomeFragment::class.java,
            ID_POPULAR to PopularFragment::class.java,
            ID_RELEASED to ReleasedFragment::class.java,
            ID_ACTRESSES to ActressesFragment::class.java,
            ID_GENRE to GenreTabsFragment::class.java
        )
    }

    @Inject lateinit var configRepository: ConfigRepository
    @Inject lateinit var dataSourceRepository: DataSourceRepository

    private lateinit var binding: ActivityMainBinding
    var currentFragment: Fragment? = null
    private var idOfDrawerItem: Long = ID_HOME
    private lateinit var fragmentManager: FragmentManager
    private var savedState: Bundle? = null
    private var firstClick: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (JAViewer.CONFIGURATIONS == null) {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
            return
        }

        JAViewer.recreateService()
        savedState = savedInstanceState
        setSupportActionBar(binding.appBarMain.toolbar)
        initFragments()
        buildDrawer()

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                appExit()
            }
        })
    }

    /** 构建 MaterialDrawer 导航抽屉（MaterialDrawer 9.x API） */
    private fun buildDrawer() {
        val slider = binding.slider

        // 设置自定义头部
        slider.headerView = layoutInflater.inflate(R.layout.drawer_header, null)

        // 添加抽屉项
        slider.itemAdapter.add(
            PrimaryDrawerItem().apply {
                identifier = ID_HOME
                name = StringHolder("主页")
                icon = ImageHolder(R.drawable.ic_menu_home)
                isIconTinted = true
            },
            PrimaryDrawerItem().apply {
                identifier = ID_FAV
                name = StringHolder("收藏夹")
                icon = ImageHolder(R.drawable.ic_menu_star)
                isIconTinted = true
                isSelectable = false
            },
            DividerDrawerItem(),
            PrimaryDrawerItem().apply {
                identifier = ID_RELEASED
                name = StringHolder("已发布")
                icon = ImageHolder(R.drawable.ic_menu_released)
                isIconTinted = true
            },
            PrimaryDrawerItem().apply {
                identifier = ID_POPULAR
                name = StringHolder("热门")
                icon = ImageHolder(R.drawable.ic_menu_popular)
                isIconTinted = true
            },
            PrimaryDrawerItem().apply {
                identifier = ID_ACTRESSES
                name = StringHolder("女优")
                icon = ImageHolder(R.drawable.ic_menu_actresses)
                isIconTinted = true
            },
            PrimaryDrawerItem().apply {
                identifier = ID_GENRE
                name = StringHolder("类别")
                icon = ImageHolder(R.drawable.ic_menu_genre)
                isIconTinted = true
            },
            DividerDrawerItem(),
            PrimaryDrawerItem().apply {
                identifier = ID_GITHUB1
                name = StringHolder("SeanChengN")
                icon = ImageHolder(R.drawable.ic_menu_github)
                isIconTinted = true
                isSelectable = false
            },
            PrimaryDrawerItem().apply {
                identifier = ID_GITHUB2
                name = StringHolder("SplashCodes")
                icon = ImageHolder(R.drawable.ic_menu_github)
                isIconTinted = true
                isSelectable = false
            },
            PrimaryDrawerItem().apply {
                identifier = ID_GITHUB3
                name = StringHolder("ccclao")
                icon = ImageHolder(R.drawable.ic_menu_github)
                isIconTinted = true
                isSelectable = false
            }
        )

        // 设置点击监听器
        slider.onDrawerItemClickListener = { _, drawerItem, _ ->
            idOfDrawerItem = drawerItem.identifier
            when (drawerItem.identifier) {
                ID_GITHUB1 -> openUrl("https://github.com/SeanChengN/JAViewer/releases")
                ID_GITHUB2 -> openUrl("https://github.com/SplashCodes/JAViewer/releases")
                ID_GITHUB3 -> openUrl("https://github.com/ccclao/JAViewer/releases")
                ID_FAV -> startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
                else -> {
                    val name = (drawerItem as? PrimaryDrawerItem)?.name?.getText(this@MainActivity)
                    if (name != null) {
                        setFragment(drawerItem.identifier.toInt(), name)
                    }
                }
            }
            false
        }

        // 设置头部按钮
        val header = slider.headerView!!
        val textSource = header.findViewById<TextView>(R.id.text_view_source)
        textSource.text = configRepository.getDataSource().toString()

        val btnSwitch = header.findViewById<MaterialButton>(R.id.btn_switch_source)
        btnSwitch.setOnClickListener { onSwitchSource() }

        // 恢复选中状态
        if (savedState != null) {
            slider.setSelection((savedState?.getInt("SelectedDrawerItemId", ID_HOME.toInt()) ?: ID_HOME.toInt()).toLong(), false)
        } else {
            slider.setSelection(ID_HOME, false)
        }
    }

    /** 初始化所有 Fragment，预创建并隐藏 */
    private fun initFragments() {
        fragmentManager = supportFragmentManager
        if (savedState != null) {
            val tag = savedState?.getString("CurrentFragment")
            currentFragment = fragmentManager.findFragmentByTag(tag)
            return
        }
        val transaction = fragmentManager.beginTransaction()
        for (fragmentClass in FRAGMENTS.values) {
            try {
                val fragment = fragmentClass.getDeclaredConstructor().newInstance()
                transaction.add(R.id.content, fragment, fragmentClass.simpleName).hide(fragment)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create fragment", e)
            }
        }
        transaction.commit()
        fragmentManager.executePendingTransactions()
    }

    /**
     * 切换显示指定 Fragment。
     *
     * @param fragment 目标 Fragment
     * @param title 工具栏标题
     */
    private fun setFragment(fragment: Fragment, title: CharSequence) {
        supportActionBar?.title = title
        val old = currentFragment
        if (old === fragment) return
        val transaction = fragmentManager.beginTransaction()
        if (old != null) transaction.hide(old)
        transaction.show(fragment)
        transaction.commit()
        currentFragment = fragment
        binding.appBarMain.appBar.elevation = if (fragment is ExtendedAppBarFragment) 0f else 4f * resources.displayMetrics.density
    }

    /** 根据 ID 切换 Fragment */
    private fun setFragment(id: Int, title: CharSequence) {
        val clazz = FRAGMENTS[id.toLong()] ?: return
        setFragment(fragmentManager.findFragmentByTag(clazz.simpleName) ?: return, title)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("CurrentFragment", currentFragment?.javaClass?.simpleName)
        outState.putInt("SelectedDrawerItemId", idOfDrawerItem.toInt())
        super.onSaveInstanceState(outState)
    }

    /** 处理返回键：先关抽屉、再关搜索、双击退出 */
    private fun appExit() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (binding.appBarMain.searchView.isSearchOpen) {
            binding.appBarMain.searchView.closeSearch()
            return
        }
        if (System.currentTimeMillis() - firstClick > 2000L) {
            firstClick = System.currentTimeMillis()
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
            return
        }
        moveTaskToBack(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_search)
        binding.appBarMain.searchView.setMenuItem(item)
        binding.appBarMain.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                try {
                    val link = configRepository.getDataSource().link ?: return false
                    startActivity(
                        MovieListActivity.newIntent(
                            this@MainActivity,
                            "$query 的搜索结果",
                            "$link${BasicService.LANGUAGE_NODE}/search/${URLEncoder.encode(query, "UTF-8")}"
                        )
                    )
                } catch (_: Exception) {
                    return false
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean = false
        })
        return true
    }

    /** 重启 Activity（用于数据源切换后刷新） */
    fun restart() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    /** 弹出数据源选择对话框 */
    fun onSwitchSource() {
        val ds = dataSourceRepository.getDataSources().toTypedArray()
        val items = ds.map { it.toString() }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("选择数据源")
            .setItems(items) { _, which ->
                val newSource = ds[which]
                if (newSource == configRepository.getDataSource()) return@setItems
                configRepository.setDataSource(newSource)
                JAViewer.recreateService()
                restart()
            }
            .create()
            .show()
    }

    /** 在浏览器中打开 URL */
    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}
