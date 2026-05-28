package io.github.javiewer.activity

import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.robertlevonyan.views.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.adapter.ActressPaletteAdapter
import io.github.javiewer.adapter.MovieHeaderAdapter
import io.github.javiewer.adapter.ScreenshotAdapter
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.adapter.item.MovieDetail
import io.github.javiewer.databinding.ActivityMovieBinding
import io.github.javiewer.network.PSVS
import io.github.javiewer.network.item.AvgleSearchResult
import io.github.javiewer.util.SimpleVideoPlayer
import io.github.javiewer.view.ViewUtil
import io.github.javiewer.viewmodel.MovieDetailViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MovieActivity : SecureActivity() {

    private lateinit var binding: ActivityMovieBinding
    private val viewModel: MovieDetailViewModel by viewModels()
    lateinit var movie: Movie
    private var video: AvgleSearchResult.Response.Video? = null
    private var mStarButton: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        movie = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("movie", Movie::class.java)!!
        } else {
            intent.getParcelableExtra("movie")!!
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = movie.title

        binding.fab.setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            intent.putExtra("keyword", movie.code)
            startActivity(intent)
        }
        binding.fab.bringToFront()

        binding.movieContent.viewPreview.setOnClickListener { onClickPreview() }
        binding.movieContent.viewPlay.setOnClickListener { onPlay() }

        // Observe ViewModel
        viewModel.detail.observe(this) { detail ->
            detail.headers.add(0, MovieDetail.Header.create("影片名", movie.title, null))
            displayInfo(detail)
            Glide.with(applicationContext).load(detail.coverUrl).into(binding.toolbarLayoutBackground)
        }

        viewModel.starred.observe(this) { isStarred ->
            if (isStarred) {
                mStarButton?.setIcon(R.drawable.ic_menu_star)
                Snackbar.make(binding.movieContent.root, "已收藏", Snackbar.LENGTH_LONG).show()
                mStarButton?.title = "取消收藏"
            } else {
                mStarButton?.setIcon(R.drawable.ic_menu_star_border)
                Snackbar.make(binding.movieContent.root, "已取消收藏", Snackbar.LENGTH_LONG).show()
                mStarButton?.title = "收藏"
            }
            FavouriteActivity.update()
        }

        // Load detail
        val movieLink = movie.link ?: return
        viewModel.loadDetail(movieLink)
    }

    private fun displayInfo(detail: MovieDetail) {
        val contentBinding = binding.movieContent
        val headersBinding = contentBinding.movieHeaders
        val screenshotsBinding = contentBinding.movieScreenshots
        val actressesBinding = contentBinding.movieActress
        val genreBinding = contentBinding.movieGenre

        // Headers
        if (detail.headers.isEmpty()) {
            headersBinding.headersRecyclerView.visibility = View.GONE
            headersBinding.headerEmptyText.visibility = View.VISIBLE
            ViewUtil.alignIconToView(headersBinding.movieIconHeader, headersBinding.headerEmptyText)
        } else {
            headersBinding.headersRecyclerView.adapter = MovieHeaderAdapter(detail.headers, this, headersBinding.movieIconHeader)
            headersBinding.headersRecyclerView.layoutManager = LinearLayoutManager(this)
            headersBinding.headersRecyclerView.isNestedScrollingEnabled = false
        }

        // Screenshots
        if (detail.screenshots.isEmpty()) {
            screenshotsBinding.screenshotsRecyclerView.visibility = View.GONE
            screenshotsBinding.screenshotsEmptyText.visibility = View.VISIBLE
            ViewUtil.alignIconToView(screenshotsBinding.movieIconScreenshots, screenshotsBinding.screenshotsEmptyText)
        } else {
            screenshotsBinding.screenshotsRecyclerView.adapter = ScreenshotAdapter(detail.screenshots, this, screenshotsBinding.movieIconScreenshots, movie)
            screenshotsBinding.screenshotsRecyclerView.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            screenshotsBinding.screenshotsRecyclerView.isNestedScrollingEnabled = false
        }

        // Actresses
        if (detail.actresses.isEmpty()) {
            actressesBinding.actressesRecyclerView.visibility = View.GONE
            actressesBinding.actressesEmptyText.visibility = View.VISIBLE
            ViewUtil.alignIconToView(actressesBinding.movieIconActresses, actressesBinding.actressesEmptyText)
        } else {
            actressesBinding.actressesRecyclerView.adapter = ActressPaletteAdapter(detail.actresses, this, actressesBinding.movieIconActresses)
            actressesBinding.actressesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            actressesBinding.actressesRecyclerView.isNestedScrollingEnabled = false
        }

        // Genre
        if (detail.genres.isEmpty()) {
            genreBinding.genreFlowLayout.visibility = View.GONE
            genreBinding.genreEmptyText.visibility = View.VISIBLE
            ViewUtil.alignIconToView(genreBinding.movieIconGenre, genreBinding.genreEmptyText)
        } else {
            for ((i, genre) in detail.genres.withIndex()) {
                val view = layoutInflater.inflate(R.layout.chip_genre, genreBinding.genreFlowLayout, false)
                val chip = view.findViewById<Chip>(R.id.chip_genre)
                chip.setOnClickListener {
                    if (genre.link != null) {
                        startActivity(MovieListActivity.newIntent(this, genre.name, genre.link!!))
                    }
                }
                chip.setChipText(genre.name)
                genreBinding.genreFlowLayout.addView(view)
                if (i == 0) ViewUtil.alignIconToView(genreBinding.movieIconGenre, view)
            }
        }

        binding.movieProgressBar.animate().setDuration(200).alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.movieProgressBar.visibility = View.GONE
            }
        }).start()

        contentBinding.root.visibility = View.VISIBLE
        contentBinding.root.y = contentBinding.root.y + 120
        contentBinding.root.alpha = 0f
        contentBinding.root.animate().translationY(0f).alpha(1f).setDuration(500).start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie, menu)
        mStarButton = menu.findItem(R.id.action_star)
        if (viewModel.isMovieStarred(movie)) {
            mStarButton?.setIcon(R.drawable.ic_menu_star)
            mStarButton?.title = "取消收藏"
        }
        mStarButton?.setOnMenuItemClickListener {
            viewModel.toggleStarMovie(movie)
            true
        }

        menu.findItem(R.id.action_share)?.setOnMenuItemClickListener {
            try {
                val cache = File(getExternalFilesDir("cache"), "screenshot")
                val os = FileOutputStream(cache)
                val screenshot = getScreenBitmap()
                screenshot.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
                val uri = FileProvider.getUriForFile(applicationContext, "io.github.javiewer.fileprovider", cache)
                val intent = Intent(Intent.ACTION_SEND)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setType("image/jpeg")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "分享此影片"))
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "无法分享：${e.message}", Toast.LENGTH_SHORT).show()
                false
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun getScreenBitmap(): Bitmap {
        val imageHeight = binding.toolbarLayoutBackground.height
        var scrollViewHeight = 0
        for (i in 0 until binding.movieContent.childCount) {
            scrollViewHeight += binding.movieContent.getChildAt(i).height
        }
        val result = Bitmap.createBitmap(binding.movieContent.width, imageHeight + scrollViewHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.parseColor("#FAFAFA"))

        val bitmap1 = Bitmap.createBitmap(binding.toolbarLayoutBackground.width, imageHeight, Bitmap.Config.ARGB_8888)
        binding.toolbarLayoutBackground.draw(Canvas(bitmap1))
        canvas.drawBitmap(bitmap1, 0f, 0f, null)

        val bitmap2 = Bitmap.createBitmap(binding.movieContent.width, scrollViewHeight, Bitmap.Config.ARGB_8888)
        binding.movieContent.draw(Canvas(bitmap2))
        canvas.drawBitmap(bitmap2, 0f, imageHeight.toFloat(), null)

        return result
    }

    @Suppress("DEPRECATION")
    fun onClickPreview() {
        if (video != null) {
            cn.jzvd.JZVideoPlayerStandard.startFullscreen(this, SimpleVideoPlayer::class.java, video!!.preview_video_url, movie.title)
            return
        }
        val dialog = ProgressDialog.show(this, "请稍后", "正在搜索该影片的预览视频", true, false)
        PSVS.INSTANCE.search(movie.code).enqueue(object : Callback<AvgleSearchResult> {
            override fun onResponse(call: Call<AvgleSearchResult>, response: Response<AvgleSearchResult>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.success && result.response.videos.isNotEmpty()) {
                        video = result.response.videos[0]
                        cn.jzvd.JZVideoPlayerStandard.startFullscreen(this@MovieActivity, SimpleVideoPlayer::class.java, video!!.preview_video_url, movie.title)
                        Toast.makeText(this@MovieActivity, "提示：预览视频可能需要科学上网", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                        return
                    }
                }
                Toast.makeText(this@MovieActivity, "该影片暂无预览", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }

            override fun onFailure(call: Call<AvgleSearchResult>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MovieActivity, "获取预览失败，请重试，或使用科学上网", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        })
    }

    @Suppress("DEPRECATION")
    fun onPlay() {
        val ts = (System.currentTimeMillis() / 1000).toString()
        if (video != null) {
            cn.jzvd.JZVideoPlayerStandard.startFullscreen(
                this, SimpleVideoPlayer::class.java,
                "http://api.rekonquer.com/psvs/mp4.php?vid=${video!!.vid}&ts=$ts&sign=${JAViewer.b(video!!.vid, ts)}",
                movie.title
            )
            return
        }
        val dialog = ProgressDialog.show(this, "请稍后", "正在搜索该影片的在线视频源", true, false)
        PSVS.INSTANCE.search(movie.code).enqueue(object : Callback<AvgleSearchResult> {
            override fun onResponse(call: Call<AvgleSearchResult>, response: Response<AvgleSearchResult>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.success && result.response.videos.isNotEmpty()) {
                        video = result.response.videos[0]
                        cn.jzvd.JZVideoPlayerStandard.startFullscreen(
                            this@MovieActivity, SimpleVideoPlayer::class.java,
                            "http://api.rekonquer.com/psvs/mp4.php?vid=${video!!.vid}&ts=$ts&sign=${JAViewer.b(video!!.vid, ts)}",
                            movie.title
                        )
                        dialog.dismiss()
                        return
                    }
                }
                Toast.makeText(this@MovieActivity, "该影片暂无在线视频源", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }

            override fun onFailure(call: Call<AvgleSearchResult>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MovieActivity, "获取在线视频源失败，请重试，或使用科学上网", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (cn.jzvd.JZVideoPlayer.backPress()) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        cn.jzvd.JZVideoPlayer.releaseAllVideos()
    }
}
