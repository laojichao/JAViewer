package io.github.javiewer.fragment.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import io.github.javiewer.JAViewer
import io.github.javiewer.adapter.ViewPagerAdapter
import io.github.javiewer.databinding.FragmentGenreBinding
import io.github.javiewer.fragment.ExtendedAppBarFragment
import io.github.javiewer.network.provider.AVMOProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GenreTabsFragment : ExtendedAppBarFragment() {

    private var _binding: FragmentGenreBinding? = null
    private val binding get() = _binding!!
    private var mAdapter: ViewPagerAdapter? = null
    private var genreJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = ViewPagerAdapter(childFragmentManager)
        binding.genreViewPager.adapter = mAdapter
        binding.genreTabs.setupWithViewPager(binding.genreViewPager)

        genreJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val html = JAViewer.SERVICE?.getGenre()?.string() ?: return@launch
                if (_binding == null) return@launch
                val genres = AVMOProvider.parseGenres(html)
                for ((title, genreList) in genres) {
                    val fragment = GenreFragment().apply {
                        getGenres().addAll(genreList)
                    }
                    mAdapter?.addFragment(fragment, title)
                }
                mAdapter?.notifyDataSetChanged()
                if (_binding != null) {
                    binding.genreProgressBar.visibility = View.GONE
                    binding.genreTabs.visibility = View.VISIBLE
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        genreJob?.cancel()
        genreJob = null
        mAdapter = null
        super.onDestroyView()
        _binding = null
    }
}
