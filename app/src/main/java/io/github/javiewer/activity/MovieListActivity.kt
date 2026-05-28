package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.databinding.ActivityMovieListBinding
import io.github.javiewer.fragment.MovieListFragment

@AndroidEntryPoint
class MovieListActivity : SecureActivity() {

    companion object {
        fun newIntent(context: Context, title: String, url: String): Intent {
            return Intent(context, MovieListActivity::class.java).apply {
                putExtra("title", title)
                putExtra("url", url)
            }
        }
    }

    private lateinit var binding: ActivityMovieListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("title")

        if (savedInstanceState == null) {
            val fragment = MovieListFragment().apply {
                arguments = Bundle().apply { putString("link", intent.getStringExtra("url")) }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_query, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
