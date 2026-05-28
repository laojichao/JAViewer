package io.github.javiewer.activity

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

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
