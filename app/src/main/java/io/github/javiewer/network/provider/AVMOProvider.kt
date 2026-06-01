package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.adapter.item.Screenshot
import org.jsoup.Jsoup

/**
 * AVMOO 主站 HTML 解析器，负责将 HTML 页面解析为领域模型。
 *
 * 使用 Jsoup 解析 HTML DOM，提取影片列表、女优列表、影片详情和类别信息。
 * 所有方法均为静态方法（通过 [JvmStatic] 暴露给 Java）。
 *
 * **注意**：解析逻辑与站点 DOM 结构强耦合，站点改版可能需要更新选择器。
 */
object AVMOProvider {

    /**
     * 解析影片列表页面 HTML。
     *
     * 选择器：`a[class*=movie-box]`，提取标题、编号、日期、封面和详情链接。
     *
     * @param html 影片列表页面 HTML
     * @return 解析后的影片列表
     */
    @JvmStatic
    fun parseMovies(html: String): List<Movie> {
        val document = Jsoup.parse(html)
        val movies = mutableListOf<Movie>()
        for (box in document.select("a[class*=movie-box]")) {
            val img = box.select("div.photo-frame > img").first() ?: continue
            val span = box.select("div.photo-info > span").first() ?: continue
            val hot = span.getElementsByTag("i").isNotEmpty()
            val date = span.select("date")
            if (date.size < 2) continue
            movies.add(
                Movie.create(
                    title = img.attr("title"),
                    code = date[0].text(),
                    date = date[1].text(),
                    coverUrl = img.attr("src"),
                    detailUrl = box.attr("href"),
                    hot = hot
                )
            )
        }
        return movies
    }

    /**
     * 解析女优列表页面 HTML。
     *
     * 选择器：`a[class*=avatar-box]`，提取名称、头像和详情链接。
     *
     * @param html 女优列表页面 HTML
     * @return 解析后的女优列表
     */
    @JvmStatic
    fun parseActresses(html: String): List<Actress> {
        val document = Jsoup.parse(html)
        val actresses = mutableListOf<Actress>()
        for (box in document.select("a[class*=avatar-box]")) {
            val img = box.select("div.photo-frame > img").first() ?: continue
            val span = box.select("div.photo-info > span").first() ?: continue
            actresses.add(
                Actress.create(
                    name = span.text(),
                    imageUrl = img.attr("src"),
                    detailUrl = box.attr("href")
                )
            )
        }
        return actresses
    }

    /**
     * 解析影片详情页面 HTML。
     *
     * 提取标题、大封面、截图列表、女优列表、头部信息和类别标签。
     *
     * @param html 影片详情页面 HTML
     * @return 解析后的 [MovieDetail] 实例
     */
    @JvmStatic
    fun parseMoviesDetail(html: String): MovieDetail {
        val document = Jsoup.parse(html)
        val movie = MovieDetail()

        movie.title = document.select("div.container > h3").first()?.text() ?: ""
        movie.coverUrl = document.select("[class=bigImage]").first()?.attr("href") ?: ""

        for (box in document.select("[class*=sample-box]")) {
            movie.screenshots.add(
                Screenshot.create(
                    box.getElementsByTag("img").first()?.attr("src") ?: "",
                    box.attr("href")
                )
            )
        }

        for (box in document.select("[class*=avatar-box]")) {
            movie.actresses.add(
                Actress.create(
                    box.text(),
                    box.getElementsByTag("img").first()?.attr("src") ?: "",
                    box.attr("href")
                )
            )
        }

        val info = document.select("div.info").first()
        if (info != null) {
            for (p in info.select("p:not([class*=header]):has(span:not([class=genre]))")) {
                val strings = p.text().split(":")
                movie.headers.add(
                    MovieDetail.Header.create(
                        strings[0].trim(),
                        strings.getOrNull(1)?.trim() ?: "",
                        null
                    )
                )
            }

            val headerNames = info.select("p[class*=header]").map { it.text().replace(":", "") }
            val headerAttr = info.select("p > a").map { arrayOf(it.text(), it.attr("href")) }
            for (i in 0 until minOf(headerNames.size, headerAttr.size)) {
                movie.headers.add(
                    MovieDetail.Header.create(
                        headerNames[i],
                        headerAttr[i][0].trim(),
                        headerAttr[i][1].trim()
                    )
                )
            }

            for (a in info.select("* > [class=genre] > a")) {
                movie.genres.add(Genre.create(a.text(), a.attr("href")))
            }
        }
        return movie
    }

    /**
     * 解析类别页面 HTML，按分组返回类别列表。
     *
     * @param html 类别页面 HTML
     * @return 以类别组标题为 key、类别列表为 value 的有序 Map
     */
    @JvmStatic
    fun parseGenres(html: String): LinkedHashMap<String, List<Genre>> {
        val map = linkedMapOf<String, List<Genre>>()
        val container = Jsoup.parse(html).getElementsByClass("pt-10").first() ?: return map
        val keys = container.getElementsByTag("h4").map { it.text() }
        val genres = container.getElementsByClass("genre-box").map { element ->
            element.getElementsByTag("a").map { a -> Genre.create(a.text(), a.attr("href")) }
        }
        for (i in 0 until minOf(keys.size, genres.size)) {
            map[keys[i]] = genres[i]
        }
        return map
    }
}
