@file:Suppress("DEPRECATION")

package io.github.javiewer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ViewPager 适配器，管理 Fragment 列表和对应标签标题。
 *
 * 使用已弃用的 [FragmentPagerAdapter]，因 AHBottomNavigationViewPager 与 ViewPager2
 * 不兼容，且此页面正在被 Compose 替代，故保留旧版实现。
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
