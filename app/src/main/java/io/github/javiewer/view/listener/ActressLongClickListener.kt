package io.github.javiewer.view.listener

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.javiewer.activity.FavouriteActivity
import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.repository.ConfigRepository
import kotlinx.coroutines.launch

/**
 * 女优长按监听器，弹出上下文菜单支持复制名称和收藏/取消收藏。
 *
 * @property actress 被长按的女优数据
 * @property activity 当前 Activity，用于显示对话框
 * @property configRepository 用户配置仓库，用于读写收藏状态
 */
class ActressLongClickListener(
    private val actress: Actress,
    private val activity: Activity?,
    private val configRepository: ConfigRepository
) : View.OnLongClickListener {

    override fun onLongClick(v: View): Boolean {
        val act = activity ?: return true

        val scope = (act as? AppCompatActivity)?.lifecycleScope ?: return true
        scope.launch {
            val isStarred = configRepository.isActressStarred(actress)
            showContextMenu(act, isStarred)
        }
        return true
    }

    private fun showContextMenu(act: Activity, isStarred: Boolean) {
        val items = if (isStarred) arrayOf("复制女优名字", "取消收藏") else arrayOf("复制女优名字", "收藏")

        AlertDialog.Builder(act)
            .setTitle(actress.name)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        val clip = act.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clip.setPrimaryClip(ClipData.newPlainText("actress", actress.name))
                        Toast.makeText(act, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        val scope = (act as? AppCompatActivity)?.lifecycleScope ?: return@setItems
                        scope.launch {
                            val nowStarred = configRepository.toggleStarActress(actress)
                            Toast.makeText(act, if (nowStarred) "已收藏" else "已取消收藏", Toast.LENGTH_SHORT).show()
                            FavouriteActivity.update()
                        }
                    }
                }
            }
            .create()
            .show()
    }
}
