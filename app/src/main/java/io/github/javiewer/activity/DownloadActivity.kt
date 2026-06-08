package io.github.javiewer.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter
import io.github.javiewer.databinding.ActivityDownloadBinding
import io.github.javiewer.fragment.DownloadFragment
import io.github.javiewer.viewmodel.DownloadViewModel

/**
 * 下载搜索 Activity，以标签页形式展示多个种子搜索站点的结果。
 *
 * 包含 4 个标签页：BTSO、Torrent Kitty、白虎、磁力蜘蛛。
 * 每 20 次下载弹出捐赠提示对话框。
 */
@AndroidEntryPoint
class DownloadActivity : SecureActivity() {

    private lateinit var binding: ActivityDownloadBinding
    private val viewModel: DownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyword = intent.getStringExtra("keyword") ?: ""

        setSupportActionBar(binding.downloadToolbar)
        supportActionBar?.title = keyword
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        val providers = listOf("btso" to "BTSO", "torrentkitty" to "Torrent Kitty", "bh" to "白虎", "btmovi" to "磁力蜘蛛")
        for ((provider, title) in providers) {
            val fragment = DownloadFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", keyword)
                    putString("provider", provider)
                }
            }
            adapter.addFragment(fragment, title)
        }

        binding.downloadViewPager.adapter = adapter
        binding.downloadTabs.setupWithViewPager(binding.downloadViewPager)

        if (savedInstanceState != null) return  // Don't re-increment on config change
        var downloadCounter = viewModel.getDownloadCounter()
        if (downloadCounter == -1L) return
        downloadCounter++
        viewModel.setDownloadCounter(downloadCounter)
        if (downloadCounter % 20 == 0L) {
            AlertDialog.Builder(this)
                .setTitle("用得不错？")
                .setMessage("您的支持是我动力来源！\n请考虑为我买杯咖啡醒醒脑，甚至其他…… ;)")
                .setPositiveButton("为我买杯咖啡") { _, _ ->
                    JAViewer.a(this)
                    AlertDialog.Builder(this)
                        .setMessage("感谢您的支持！;)\n新功能持续开发中！")
                        .setPositiveButton("确认", null)
                        .show()
                }
                .setNeutralButton("不再显示") { _, _ ->
                    viewModel.setDownloadCounter(-1)
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
