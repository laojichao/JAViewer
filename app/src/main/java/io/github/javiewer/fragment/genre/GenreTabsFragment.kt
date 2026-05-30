package io.github.javiewer.fragment.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter
import io.github.javiewer.adapter.item.Genre
import io.github.javiewer.databinding.FragmentGenreBinding
import io.github.javiewer.fragment.ExtendedAppBarFragment
import io.github.javiewer.network.provider.AVMOProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenreTabsFragment : ExtendedAppBarFragment() {

    private var _binding: FragmentGenreBinding? = null
    private val binding get() = _binding!!
    private var mAdapter: ViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = ViewPagerAdapter(childFragmentManager)
        binding.genreViewPager.adapter = mAdapter
        binding.genreTabs.setupWithViewPager(binding.genreViewPager)

        JAViewer.SERVICE?.getGenre()?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.genreProgressBar.visibility = View.GONE
                try {
                    val html = response.body()?.string() ?: return
                    val genres = AVMOProvider.parseGenres(html)
                    for ((title, genreList) in genres) {
                        val fragment = GenreFragment().apply {
                            getGenres().addAll(genreList)
                        }
                        mAdapter?.addFragment(fragment, title)
                    }
                    mAdapter?.notifyDataSetChanged()
                    binding.genreTabs.visibility = View.VISIBLE
                } catch (e: Throwable) {
                    onFailure(call, e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
