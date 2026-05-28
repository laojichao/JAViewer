package io.github.javiewer.fragment.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import io.github.javiewer.R
import io.github.javiewer.adapter.ViewPagerAdapter
import io.github.javiewer.databinding.FragmentFavouriteBinding
import io.github.javiewer.fragment.ExtendedAppBarFragment

class FavouriteTabsFragment : ExtendedAppBarFragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private var mAdapter: ViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAdapter = ViewPagerAdapter(childFragmentManager)
        binding.favouriteViewPager.adapter = mAdapter
        binding.favouriteTabs.setupWithViewPager(binding.favouriteViewPager)
        mAdapter?.addFragment(FavouriteMovieFragment(), "作品")
        mAdapter?.addFragment(FavouriteActressFragment(), "女优")
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
