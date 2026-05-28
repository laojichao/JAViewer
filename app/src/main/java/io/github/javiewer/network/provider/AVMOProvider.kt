package io.github.javiewer.network.provider

import io.github.javiewer.adapter.item.Actress
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.adapter.item.Screenshot
import org.jsoup.Jsoup

object AVMOProvider {

    @JvmStatic
    fun parseMovies(html: String): List<Movie> {
        val document = Jsoup.parse(html)
        val movies = mutableListOf<Movie>()
        for (box in document.select("a[class*=movie-box]")) {
            val img = box.select("div.photo-frame > img").first() ?: continue
            val span = box.select("div.photo-info > span").first() ?: continue
            val hot = span.getElementsByTag("i").isNotEmpty()
            val date = span.select("date")
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
