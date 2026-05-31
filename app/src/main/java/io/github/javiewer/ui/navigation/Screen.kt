package io.github.javiewer.ui.navigation

sealed class Screen(val route: String) {
    data object Start : Screen("start")
    data object Home : Screen("home")
    data object Popular : Screen("popular")
    data object Released : Screen("released")
    data object Actresses : Screen("actresses")
    data object Genres : Screen("genres")
    data object Favorites : Screen("favorites")
    data object Download : Screen("download/{keyword}") {
        fun createRoute(keyword: String) = "download/$keyword"
    }
    data object MovieDetail : Screen("movie/{movieLink}") {
        fun createRoute(movieLink: String) = "movie/${java.net.URLEncoder.encode(movieLink, "UTF-8")}"
    }
    data object MovieList : Screen("movieList/{title}/{url}") {
        fun createRoute(title: String, url: String) =
            "movieList/${java.net.URLEncoder.encode(title, "UTF-8")}/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
    data object Gallery : Screen("gallery/{position}/{imageUrls}") {
        fun createRoute(position: Int, imageUrls: String) =
            "gallery/$position/${java.net.URLEncoder.encode(imageUrls, "UTF-8")}"
    }
    data object WebView : Screen("webView/{url}") {
        fun createRoute(url: String) = "webView/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
}
