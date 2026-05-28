package io.github.javiewer.adapter.item

data class MovieDetail(
    var title: String = "",
    var coverUrl: String = "",
    val headers: MutableList<Header> = mutableListOf(),
    val screenshots: MutableList<Screenshot> = mutableListOf(),
    val genres: MutableList<Genre> = mutableListOf(),
    val actresses: MutableList<Actress> = mutableListOf()
) {
    data class Header(
        var name: String = "",
        var value: String = ""
    ) : Linkable() {
        companion object {
            @JvmStatic
            fun create(name: String, value: String?, link: String?): Header {
                return Header(name = name, value = value ?: "").apply { this.link = link }
            }
        }
    }
}
