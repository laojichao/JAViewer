package io.github.javiewer.view.listener

import android.app.Activity
import android.view.View
import io.github.javiewer.activity.MovieListActivity
import io.github.javiewer.adapter.item.Actress

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
