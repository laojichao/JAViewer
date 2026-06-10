package io.github.javiewer.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.data.migration.JsonToRoomMigrator
import io.github.javiewer.repository.DataSourceRepository
import io.github.javiewer.repository.PropertiesRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * 启动/闪屏 Activity，负责应用初始化流程。
 *
 * 初始化步骤：
 * 1. 加载旧版 JSON 配置文件
 * 2. 触发 JSON 到 Room 的数据迁移
 * 3. 从 GitHub 获取远程配置（失败时回退到本地）
 * 4. 初始化数据源和域名重写映射
 * 5. 检查应用更新
 * 6. 跳转到 [MainActivity]
 *
 * 使用 Hilt 注入 [PropertiesRepository]、[DataSourceRepository]、[JsonToRoomMigrator]。
 */
@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    @Inject lateinit var propertiesRepository: PropertiesRepository
    @Inject lateinit var dataSourceRepository: DataSourceRepository
    @Inject lateinit var migrator: JsonToRoomMigrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        init()
    }

    /** 初始化应用：加载配置、触发迁移、获取远程属性 */
    private fun init() {
        val config = File(JAViewer.getStorageDir(this), "configurations.json")

        val noMedia = File(JAViewer.getStorageDir(this), ".nomedia")
        try {
            noMedia.createNewFile()
        } catch (_: IOException) {
        }

        JAViewer.CONFIGURATIONS = io.github.javiewer.Configurations.load(config)
        lifecycleScope.launch {
            migrator.migrateIfNeeded()
            loadProperties()
        }
    }

    /** 异步加载远程配置 */
    private fun loadProperties() {
        lifecycleScope.launch {
            val properties = propertiesRepository.fetchProperties()
            if (properties != null && !isFinishing) {
                handleProperties(properties)
            }
        }
    }

    /** 处理远程配置：初始化数据源、检查更新 */
    private fun handleProperties(properties: io.github.javiewer.Properties) {
        if (isFinishing || isDestroyed) return

        val sources = properties.getDataSources()
        dataSourceRepository.setDataSources(sources)
        JAViewer.DATA_SOURCES.clear()
        JAViewer.DATA_SOURCES.addAll(sources)

        JAViewer.hostReplacements.clear()
        JAViewer.hostReplacements.putAll(dataSourceRepository.getHostReplacements())

        val currentVersion = try {
            val info = packageManager.getPackageInfo(packageName, 0)
            androidx.core.content.pm.PackageInfoCompat.getLongVersionCode(info).toInt()
        } catch (_: PackageManager.NameNotFoundException) {
            0 // 无法获取版本号时返回默认值
        }

        if (properties.getLatestVersionCode() > 0 && currentVersion < properties.getLatestVersionCode()) {
            var message = "新版本：${properties.getLatestVersion()}"
            properties.changelog?.let {
                message += "\n\n更新日志：\n\n$it\n"
            }
            AlertDialog.Builder(this)
                .setTitle("发现更新")
                .setMessage(message)
                .setNegativeButton("忽略更新") { _, _ -> start() }
                .setPositiveButton("更新") { _, _ ->
                    start()
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ccclao/JAViewer/releases")))
                }
                .create()
                .show()
        } else {
            start()
        }
    }

    /** 跳转到主页 */
    private fun start() {
        JAViewer.recreateService()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
