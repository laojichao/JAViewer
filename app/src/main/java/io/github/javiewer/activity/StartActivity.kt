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
        }
        loadProperties()
    }

    private fun loadProperties() {
        lifecycleScope.launch {
            val properties = propertiesRepository.fetchProperties()
            if (properties != null && !isFinishing) {
                handleProperties(properties)
            }
        }
    }

    private fun handleProperties(properties: io.github.javiewer.Properties) {
        if (isFinishing || isDestroyed) return

        val sources = properties.getDataSources()
        dataSourceRepository.setDataSources(sources)
        JAViewer.DATA_SOURCES.clear()
        JAViewer.DATA_SOURCES.addAll(sources)

        JAViewer.hostReplacements.clear()
        JAViewer.hostReplacements.putAll(dataSourceRepository.getHostReplacements())

        val currentVersion = try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (_: PackageManager.NameNotFoundException) {
            throw RuntimeException("Hacked???")
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

    private fun start() {
        JAViewer.recreateService()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
