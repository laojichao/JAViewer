package io.github.javiewer.view.listener

import android.app.Activity
import android.view.View
import io.github.javiewer.activity.MovieListActivity
import io.github.javiewer.adapter.item.Actress

/**
 * 女优点击监听器，点击后跳转到该女优的作品列表页。
 *
 * @property actress 被点击的女优数据
 * @property activity 当前 Activity，用于启动新页面
 */
class ActressClickListener(
    private val actress: Actress,
    private val activity: Activity?
) : View.OnClickListener {

    override fun onClick(v: View) {
        if (actress.link != null) {
            activity?.startActivity(MovieListActivity.newIntent(activity, "${actress.name} 的作品", actress.link!!))
        }
    }
}
