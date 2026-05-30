package io.github.javiewer.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialize.util.UIUtils
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
import io.github.javiewer.view.SimpleSearchView
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : SecureActivity() {

    companion object {
        const val ID_HOME = 1L
        const val ID_FAV = 2L
        const val ID_POPULAR = 3L
        const val ID_RELEASED = 4L
        const val ID_ACTRESSES = 5L
        const val ID_GENRE = 6L
        const val ID_GITHUB1 = 7L
        const val ID_GITHUB2 = 8L
        const val ID_GITHUB3 = 9L

        val FRAGMENTS = mapOf(
            ID_HOME to HomeFragment::class.java,
            ID_POPULAR to PopularFragment::class.java,
            ID_RELEASED to ReleasedFragment::class.java,
            ID_ACTRESSES to ActressesFragment::class.java,
            ID_GENRE to GenreTabsFragment::class.java
        )
    }

    private lateinit var binding: ActivityMainBinding
    var currentFragment: Fragment? = null
    private var idOfDrawerItem: Long = ID_HOME
    private lateinit var fragmentManager: FragmentManager
    private var savedState: Bundle? = null
    private var mDrawer: Drawer? = null
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
    }

    private fun buildDrawer() {
        val result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(binding.appBarMain.toolbar)
            .withHeader(R.layout.drawer_header)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(ID_HOME).withName("主页").withIcon(R.drawable.ic_menu_home).withIconTintingEnabled(true),
                PrimaryDrawerItem().withIdentifier(ID_FAV).withName("收藏夹").withTag("Fav").withIcon(R.drawable.ic_menu_star).withIconTintingEnabled(true).withSelectable(false),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(ID_RELEASED).withName("已发布").withIcon(R.drawable.ic_menu_released).withIconTintingEnabled(true),
                PrimaryDrawerItem().withIdentifier(ID_POPULAR).withName("热门").withIcon(R.drawable.ic_menu_popular).withIconTintingEnabled(true),
                PrimaryDrawerItem().withIdentifier(ID_ACTRESSES).withName("女优").withIcon(R.drawable.ic_menu_actresses).withIconTintingEnabled(true),
                PrimaryDrawerItem().withIdentifier(ID_GENRE).withName("类别").withIcon(R.drawable.ic_menu_genre).withIconTintingEnabled(true),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(ID_GITHUB1).withName("SeanChengN").withTag("Github").withIcon(R.drawable.ic_menu_github).withIconTintingEnabled(true).withSelectable(false),
                PrimaryDrawerItem().withIdentifier(ID_GITHUB2).withName("SplashCodes").withTag("Github").withIcon(R.drawable.ic_menu_github).withIconTintingEnabled(true).withSelectable(false),
                PrimaryDrawerItem().withIdentifier(ID_GITHUB3).withName("ccclao").withTag("Github").withIcon(R.drawable.ic_menu_github).withIconTintingEnabled(true).withSelectable(false)
            )
            .withSelectedItem(ID_HOME)
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
                    idOfDrawerItem = drawerItem.identifier
                    when (drawerItem.identifier) {
                        ID_GITHUB1 -> openUrl("https://github.com/SeanChengN/JAViewer/releases")
                        ID_GITHUB2 -> openUrl("https://github.com/SplashCodes/JAViewer/releases")
                        ID_GITHUB3 -> openUrl("https://github.com/ccclao/JAViewer/releases")
                        ID_FAV -> startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
                        else -> {
                            if (drawerItem is AbstractBadgeableDrawerItem<*>) {
                                setFragment(drawerItem.identifier.toInt(), drawerItem.name.text)
                            }
                        }
                    }
                    return false
                }
            })
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val guideline = result.header.findViewById<Guideline>(R.id.guideline_status_bar)
            guideline.setGuidelineBegin(UIUtils.getStatusBarHeight(this, true))
        }

        mDrawer = result

        val textSource = result.header.findViewById<TextView>(R.id.text_view_source)
        textSource.text = JAViewer.getDataSource().toString()

        val btnSwitch = result.header.findViewById<MaterialButton>(R.id.btn_switch_source)
        btnSwitch.setOnClickListener { onSwitchSource() }

        if (savedState != null) {
            result.setSelection(savedState!!.getInt("SelectedDrawerItemId", ID_HOME.toInt()).toLong())
        } else {
            result.setSelection(ID_HOME)
        }
    }

    private fun initFragments() {
        fragmentManager = supportFragmentManager
        if (savedState != null) {
            val tag = savedState!!.getString("CurrentFragment")
            currentFragment = fragmentManager.findFragmentByTag(tag)
            return
        }
        val transaction = fragmentManager.beginTransaction()
        for (fragmentClass in FRAGMENTS.values) {
            try {
                val fragment = fragmentClass.getDeclaredConstructor().newInstance()
                transaction.add(R.id.content, fragment, fragmentClass.simpleName).hide(fragment)
            } catch (_: Exception) {
            }
        }
        transaction.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun setFragment(fragment: Fragment, title: CharSequence) {
        supportActionBar?.title = title
        val old = currentFragment
        if (old === fragment) return
        val transaction = fragmentManager.beginTransaction()
        if (old != null) transaction.hide(old)
        transaction.show(fragment)
        transaction.commit()
        currentFragment = fragment
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.appBarMain.appBar.elevation = if (fragment is ExtendedAppBarFragment) 0f else 4f * resources.displayMetrics.density
        }
    }

    private fun setFragment(id: Int, title: CharSequence) {
        val clazz = FRAGMENTS[id.toLong()] ?: return
        setFragment(fragmentManager.findFragmentByTag(clazz.simpleName) ?: return, title)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("CurrentFragment", currentFragment?.javaClass?.simpleName)
        outState.putInt("SelectedDrawerItemId", idOfDrawerItem.toInt())
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        appExit()
    }

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
                    startActivity(
                        MovieListActivity.newIntent(
                            this@MainActivity,
                            "$query 的搜索结果",
                            "${JAViewer.getDataSource().link}${BasicService.LANGUAGE_NODE}/search/${URLEncoder.encode(query, "UTF-8")}"
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

    fun restart() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    fun onSwitchSource() {
        val ds = JAViewer.DATA_SOURCES.toTypedArray()
        val items = ds.map { it.toString() }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("选择数据源")
            .setItems(items) { _, which ->
                val newSource = JAViewer.DATA_SOURCES[which]
                if (newSource == JAViewer.getDataSource()) return@setItems
                JAViewer.CONFIGURATIONS?.setDataSource(newSource)
                JAViewer.CONFIGURATIONS?.save()
                restart()
            }
            .create()
            .show()
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}
