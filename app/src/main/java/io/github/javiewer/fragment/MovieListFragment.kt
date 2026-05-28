package io.github.javiewer.fragment

import android.os.Bundle
import io.github.javiewer.JAViewer
import okhttp3.ResponseBody
import retrofit2.Call

class MovieListFragment : MovieFragment() {

    private var link: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = arguments?.getString("link") ?: ""
    }

    override fun newCall(page: Int): Call<ResponseBody>? = JAViewer.SERVICE?.get("$link/page/$page")
}
