package io.github.javiewer.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import io.github.javiewer.adapter.item.DownloadLink
import io.github.javiewer.adapter.item.MagnetLink
import io.github.javiewer.databinding.LayoutDownloadBinding
import io.github.javiewer.network.provider.DownloadLinkProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloadLinkAdapter(
    items: MutableList<DownloadLink>,
    private val activity: Activity?,
    private val provider: DownloadLinkProvider
) : ItemAdapter<DownloadLink, DownloadLinkAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val link = getItems()[position]
        holder.bind(link)
        holder.binding.layoutDownload.setOnClickListener {
            if (!link.hasMagnetLink()) {
                val act = activity ?: return@setOnClickListener
                val dialog = ProgressDialog(act).apply {
                    setTitle("请稍后")
                    setMessage("正在获取磁力链接")
                    setIndeterminate(false)
                    setCancelable(false)
                    show()
                }
                val linkUrl = link.link ?: return@setOnClickListener
                provider.get(linkUrl)?.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        try {
                            val magnetLink = provider.parseMagnetLink(response.body()?.string() ?: "")
                            onMagnetGet(magnetLink?.magnetLink)
                        } catch (e: Throwable) {
                            onFailure(call, e)
                        }
                        if (activity != null && !activity.isFinishing) dialog.dismiss()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        if (activity != null && !activity.isFinishing) dialog.dismiss()
                    }
                })
            } else {
                onMagnetGet(link.getMagnetLinkStr())
            }
        }
    }

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
