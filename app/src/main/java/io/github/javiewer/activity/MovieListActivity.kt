package io.github.javiewer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.R
import io.github.javiewer.databinding.ActivityMovieListBinding
import io.github.javiewer.fragment.MovieListFragment

/**
 * 通用影片列表 Activity，用于展示搜索结果、类别影片或女优作品。
 *
 * 通过 [newIntent] 工厂方法创建 Intent，传入标题和 URL。
 * 内部托管 [MovieListFragment] 展示影片列表。
 */
@AndroidEntryPoint
class MovieListActivity : SecureActivity() {

    companion object {
        /**
         * 创建启动 Intent。
         *
         * @param context 上下文
         * @param title 页面标题
         * @param url 影片列表页 URL
         * @return 配置好的 Intent
         */
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
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
