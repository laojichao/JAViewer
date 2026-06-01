package io.github.javiewer.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.javiewer.activity.FavouriteActivity
import io.github.javiewer.activity.MovieActivity
import io.github.javiewer.ui.screen.HomeScreen

/**
 * JAViewer 应用的 Navigation Compose 导航图。
 *
 * 定义所有 Compose 屏幕的路由和导航逻辑。
 * 部分屏幕（影片详情、收藏夹）仍跳转到旧版 XML Activity，
 * 待后续迁移完成后统一为 Compose 屏幕。
 *
 * @param navController 导航控制器
 * @param startDestination 起始路由，默认为 [Screen.Home]
 */
@Composable
fun JaviewerNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { movie ->
                    val intent = Intent(context, MovieActivity::class.java)
                    intent.putExtra("movie", movie)
                    context.startActivity(intent)
                },
                onFavoritesClick = {
                    context.startActivity(Intent(context, FavouriteActivity::class.java))
                },
                onSearch = { query ->
                    // Handle search
                }
            )
        }

        composable(
            route = Screen.MovieList.route,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("MovieList: $title")
            }
        }
    }
}
