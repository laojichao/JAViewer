package io.github.javiewer.activity

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

/**
 * 安全 Activity 基类，在暂停时设置 FLAG_SECURE 防止截屏。
 *
 * 所有需要防截屏保护的 Activity 应继承此类。
 * 恢复时自动清除 FLAG_SECURE 以允许正常截屏。
 */
open class SecureActivity : AppCompatActivity() {

    override fun onPause() {
        super.onPause()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onResume() {
        super.onResume()
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
