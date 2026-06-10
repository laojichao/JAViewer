@file:Suppress("DEPRECATION")

package io.github.javiewer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ViewPager 适配器，管理 Fragment 列表和对应标签标题。
 *
 * 使用已弃用的 [FragmentPagerAdapter]，用于旧版 ViewPager（非 ViewPager2）。
 * 适用于 [io.github.javiewer.activity.DownloadActivity]、
 * [io.github.javiewer.fragment.favourite.FavouriteTabsFragment]、
 * [io.github.javiewer.fragment.genre.GenreTabsFragment]。
 *
 * @param manager FragmentManager
 */
class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    /**
     * 添加 Fragment 及其标签标题。
     *
     * @param fragment 要添加的 Fragment
     * @param title 标签标题
     */
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence = titleList[position]
}
