package io.github.javiewer.ui.navigation

/**
 * Navigation Compose 路由定义，使用密封类确保类型安全。
 *
 * 每个子对象对应一个屏幕目的地，[route] 为 Navigation 路由字符串。
 * 带参数的路由提供 [createRoute] 工厂方法进行 URL 编码。
 */
sealed class Screen(val route: String) {
    /** 启动/闪屏页 */
    data object Start : Screen("start")

    /** 首页（主页/已发布/热门） */
    data object Home : Screen("home")

    /** 热门影片页 */
    data object Popular : Screen("popular")

    /** 最新发布页 */
    data object Released : Screen("released")

    /** 女优列表页 */
    data object Actresses : Screen("actresses")

    /** 类别页 */
    data object Genres : Screen("genres")

    /** 收藏夹页 */
    data object Favorites : Screen("favorites")

    /** 下载搜索页 */
    data object Download : Screen("download/{keyword}") {
        /**
         * 创建下载页路由。
         * @param keyword 搜索关键词
         */
        fun createRoute(keyword: String) = "download/$keyword"
    }

    /** 影片详情页 */
    data object MovieDetail : Screen("movie/{movieLink}") {
        /**
         * 创建影片详情页路由。
         * @param movieLink 影片详情页 URL（自动 URL 编码）
         */
        fun createRoute(movieLink: String) = "movie/${java.net.URLEncoder.encode(movieLink, "UTF-8")}"
    }

    /** 影片列表页（搜索结果/类别影片/女优影片） */
    data object MovieList : Screen("movieList/{title}/{url}") {
        /**
         * 创建影片列表页路由。
         * @param title 页面标题
         * @param url 影片列表页 URL
         */
        fun createRoute(title: String, url: String) =
            "movieList/${java.net.URLEncoder.encode(title, "UTF-8")}/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }

    /** 图片画廊页 */
    data object Gallery : Screen("gallery/{position}/{imageUrls}") {
        /**
         * 创建画廊页路由。
         * @param position 初始图片索引
         * @param imageUrls 图片 URL 列表（JSON 编码）
         */
        fun createRoute(position: Int, imageUrls: String) =
            "gallery/$position/${java.net.URLEncoder.encode(imageUrls, "UTF-8")}"
    }

    /** WebView 页 */
    data object WebView : Screen("webView/{url}") {
        /**
         * 创建 WebView 页路由。
         * @param url 要加载的 URL
         */
        fun createRoute(url: String) = "webView/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
}
