package io.github.javiewer.view.listener

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.github.javiewer.JAViewer
import io.github.javiewer.activity.FavouriteActivity
import io.github.javiewer.adapter.item.Actress

class ActressLongClickListener(
    private val actress: Actress,
    private val activity: Activity?
) : View.OnLongClickListener {

    override fun onLongClick(v: View): Boolean {
        val actresses = JAViewer.CONFIGURATIONS?.getStarredActresses() ?: return true
        val contain = actresses.contains(actress)
        val items = if (contain) arrayOf("复制女优名字", "取消收藏") else arrayOf("复制女优名字", "收藏")

        AlertDialog.Builder(activity!!)
            .setTitle(actress.name)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        val clip = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clip.setPrimaryClip(ClipData.newPlainText("actress", actress.name))
                        Toast.makeText(activity, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        if (contain) {
                            actresses.remove(actress)
                            Toast.makeText(activity, "已取消收藏", Toast.LENGTH_SHORT).show()
                        } else {
                            actresses.reverse()
                            actresses.add(actress)
                            actresses.reverse()
                            Toast.makeText(activity, "已收藏", Toast.LENGTH_SHORT).show()
                        }
                        JAViewer.CONFIGURATIONS?.save()
                        FavouriteActivity.update()
                    }
                }
            }
            .create()
            .show()
        return true
    }
}
