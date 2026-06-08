package io.github.javiewer.adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.databinding.LayoutDownloadBinding
import io.github.javiewer.network.provider.DownloadLinkProvider
import kotlinx.coroutines.launch

/**
 * 下载链接列表适配器，展示资源条目并处理磁力链接获取。
 *
 * 点击条目时：若已有磁力链接则直接弹出操作对话框；
 * 否则通过协程异步获取磁力链接后弹出对话框。
 *
 * @param items 下载链接数据列表
 * @param activity 当前 Activity
 * @param provider 下载链接提供者，用于获取磁力链接
 */
class DownloadLinkAdapter(
    items: MutableList<DownloadLink>,
    private val activity: Activity?,
    private val provider: DownloadLinkProvider
) : ItemAdapter<DownloadLink, DownloadLinkAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val link = getItems()[position]
        holder.bind(link)
        holder.binding.layoutDownload.setOnClickListener {
            if (!link.hasMagnetLink()) {
                val act = activity ?: return@setOnClickListener
                val density = act.resources.displayMetrics.density
                val padding = (20 * density).toInt()
                val layout = LinearLayout(act).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    setPadding(padding, padding, padding, padding)
                }
                layout.addView(ProgressBar(act).apply { isIndeterminate = true })
                layout.addView(TextView(act).apply {
                    text = "正在获取磁力链接"
                    setPadding((16 * density).toInt(), 0, 0, 0)
                })
                val dialog = AlertDialog.Builder(act)
                    .setTitle("请稍后")
                    .setView(layout)
                    .setCancelable(false)
                    .show()
                val linkUrl = link.link ?: return@setOnClickListener
                val scope = (act as? AppCompatActivity)?.lifecycleScope ?: return@setOnClickListener
                scope.launch {
                    try {
                        val body = provider.get(linkUrl)
                        val magnetLink = provider.parseMagnetLink(body?.string() ?: "")
                        onMagnetGet(magnetLink?.magnetLink)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    if (!act.isFinishing) dialog.dismiss()
                }
            } else {
                onMagnetGet(link.getMagnetLinkStr())
            }
        }
    }

    /** 弹出磁力链接操作对话框（复制/打开/取消） */
    private fun onMagnetGet(magnetLink: String?) {
        val act = activity ?: return
        if (!magnetLink.isNullOrEmpty()) {
            AlertDialog.Builder(act)
                .setTitle("磁力链接")
                .setMessage(magnetLink)
                .setNeutralButton("复制到剪贴板") { _, _ ->
                    val clip = act.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clip.setPrimaryClip(ClipData.newPlainText("magnet-link", magnetLink))
                    Toast.makeText(act, "磁力链接：$magnetLink 已复制到剪贴板", Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton("打开") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(magnetLink))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    act.startActivity(intent)
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            Toast.makeText(act, "磁力链接获取失败", Toast.LENGTH_SHORT).show()
        }
    }

    class ViewHolder(val binding: LayoutDownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(link: DownloadLink) {
            binding.downloadTitle.text = link.title
            binding.downloadSize.text = link.size
            binding.downloadDate.text = link.date
        }
    }
}
