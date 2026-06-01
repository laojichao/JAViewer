package io.github.javiewer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.ui.navigation.JaviewerNavGraph
import io.github.javiewer.ui.theme.JaviewerTheme

/**
 * Compose 版主 Activity，作为单 Activity 架构的入口。
 *
 * 使用 [JaviewerTheme] 应用 Material 3 主题，
 * 通过 [JaviewerNavGraph] 管理所有 Compose 屏幕的导航。
 * 启用 Edge-to-Edge 显示以支持沉浸式状态栏。
 *
 * 与旧版 [io.github.javiewer.activity.MainActivity]（XML）共存，
 * 待全部屏幕迁移完成后将移除旧版。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JaviewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    JaviewerNavGraph(navController = navController)
                }
            }
        }
    }
}
