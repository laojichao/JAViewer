package io.github.javiewer.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.Configurations
import io.github.javiewer.JAViewer
import io.github.javiewer.Properties
import io.github.javiewer.R
import io.github.javiewer.adapter.item.DataSource
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        checkPermissions()
    }

    private fun readProperties() {
        val url = "https://raw.githubusercontent.com/ipcjs/JAViewer/master/app/src/main/assets/properties.json"
        val request = Request.Builder()
            .url("$url?t=${System.currentTimeMillis() / 1000}")
            .build()
        JAViewer.httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                readLocalProperties()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body() ?: run { readLocalProperties(); return }
                    val properties = JAViewer.parseJson(Properties::class.java, body.string())
                    if (properties != null) {
                        Handler(Looper.getMainLooper()).post { handleProperties(properties) }
                    }
                } catch (_: IOException) {
                    readLocalProperties()
                } finally {
                    response.close()
                }
            }
        })
    }

    private fun readLocalProperties() {
        try {
            val json = assets.open("properties.json").bufferedReader().use { it.readText() }
            val properties = JAViewer.parseJson(Properties::class.java, json)
            if (properties != null) {
                Handler(Looper.getMainLooper()).post { handleProperties(properties) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleProperties(properties: Properties) {
        JAViewer.DATA_SOURCES.clear()
        JAViewer.DATA_SOURCES.addAll(properties.getDataSources())

        JAViewer.hostReplacements.clear()
        for (source in JAViewer.DATA_SOURCES) {
            try {
                val host = URI(source.link).host
                source.legacies?.forEach { legacy ->
                    JAViewer.hostReplacements[legacy] = host
                }
            } catch (_: URISyntaxException) {
            }
        }

        val currentVersion = try {
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        checkPermissions()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        AlertDialog.Builder(this@StartActivity)
                            .setTitle("权限申请")
                            .setCancelable(false)
                            .setMessage("JAViewer 需要储存空间权限，储存用户配置。请您允许。")
                            .setPositiveButton(android.R.string.ok) { _, _ -> checkPermissions() }
                            .show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                })
                .onSameThread()
                .check()
            return
        }

        val oldConfig = File(getExternalFilesDir(null), "configurations.json")
        val config = File(JAViewer.getStorageDir(), "configurations.json")
        if (oldConfig.exists()) {
            oldConfig.renameTo(config)
        }

        val noMedia = File(JAViewer.getStorageDir(), ".nomedia")
        try {
            noMedia.createNewFile()
        } catch (_: IOException) {
        }

        JAViewer.CONFIGURATIONS = Configurations.load(config)
        readProperties()
    }
}
