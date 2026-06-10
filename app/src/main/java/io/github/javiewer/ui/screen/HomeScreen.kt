package io.github.javiewer.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.ui.component.MovieCard
import io.github.javiewer.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * 首页 Compose 屏幕，替代旧版 [io.github.javiewer.activity.MainActivity] + [io.github.javiewer.fragment.HomeFragment]。
 *
 * 包含导航抽屉（主页/收藏夹/已发布/热门/女优/类别）和影片列表，
 * 支持下拉刷新和无限滚动分页。
 *
 * @param onMovieClick 影片点击回调
 * @param onFavoritesClick 收藏夹点击回调
 * @param onSearch 搜索提交回调
 * @param viewModel 首页 ViewModel，通过 Hilt 自动注入
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit,
    onSearch: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Show error as Toast
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val tabs = listOf("主页", "已发布", "热门", "女优")
    val drawerItems = listOf(
        "主页" to { viewModel.loadTab(0) },
        "收藏夹" to { onFavoritesClick() },
        "已发布" to { viewModel.loadTab(1) },
        "热门" to { viewModel.loadTab(2) },
        "女优" to { viewModel.loadTab(3) },
        "类别" to { Toast.makeText(context, "类别功能暂未迁移至 Compose 版本", Toast.LENGTH_SHORT).show() }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "JAViewer",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                drawerItems.forEach { (name, action) ->
                    NavigationDrawerItem(
                        label = { Text(name) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            action()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tabs.getOrElse(currentTab) { "主页" }) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            MovieList(
                movies = movies,
                isLoading = isLoading,
                onMovieClick = onMovieClick,
                onLoadMore = { viewModel.loadNextPage() },
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

/**
 * 影片列表组件，支持下拉刷新和无限滚动。
 *
 * @param movies 影片列表数据
 * @param isLoading 是否正在加载
 * @param onMovieClick 影片点击回调
 * @param onLoadMore 触发加载更多回调
 * @param onRefresh 下拉刷新回调
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieList(
    movies: List<Movie>,
    isLoading: Boolean,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Infinite scroll detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount
                if (lastVisible >= totalItems - 5 && !isLoading) {
                    onLoadMore()
                }
            }
    }

    PullToRefreshBox(
        isRefreshing = isLoading && movies.isEmpty(),
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        if (movies.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无数据", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies, key = { it.code }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) }
                    )
                }
            }
        }
    }
}
