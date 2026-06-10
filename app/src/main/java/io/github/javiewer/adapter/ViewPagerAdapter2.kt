package io.github.javiewer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * ViewPager2 适配器，管理 Fragment 列表和对应标签标题。
 *
 * 使用 [FragmentStateAdapter] 适配 ViewPager2。
 * 适用于 [io.github.javiewer.activity.FavouriteActivity]。
 *
 * @param activity 宿主 Activity
 */
class ViewPagerAdapter2(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

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

    /** 获取指定位置的标签标题 */
    fun getPageTitle(position: Int): CharSequence = titleList[position]
}
