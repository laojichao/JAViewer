package io.github.javiewer.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import io.github.javiewer.JAViewer
import io.github.javiewer.R
import io.github.javiewer.adapter.item.Movie
import io.github.javiewer.databinding.ActivityGalleryBinding
import io.github.javiewer.databinding.ContentGalleryBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 图片画廊 Activity，全屏展示影片截图。
 *
 * 支持左右滑动切换图片、单击显示/隐藏工具栏、自动隐藏工具栏、
 * 保存图片到本地存储。使用沉浸式模式隐藏系统导航栏。
 */
@AndroidEntryPoint
class GalleryActivity : SecureActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var imageUrls: Array<String>
    private var movie: Movie? = null
    private var mVisible = false
    private val handler = Handler(Looper.getMainLooper())

    private val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 150 }
    private val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 150 }

    private val hidePart2Runnable = Runnable {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
        binding.toolbarGallery.startAnimation(fadeIn)
    }

    private val hideRunnable = Runnable { hide() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarGallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                toggle()
                return true
            }
        })
        binding.galleryPager.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            false
        }

        supportActionBar?.hide()
        binding.toolbarGallery.startAnimation(fadeOut)
        mVisible = false
        hidePart2Runnable.run()

        val bundle = intent.extras ?: run { finish(); return }
        imageUrls = bundle.getStringArray("urls") ?: emptyArray()
        binding.galleryPager.adapter = ImageAdapter(this, imageUrls)
        binding.galleryPager.currentItem = bundle.getInt("position")
        binding.galleryPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                updateIndicator()
            }
        })
        updateIndicator()
        movie = androidx.core.os.BundleCompat.getParcelable(bundle, "movie", Movie::class.java)
    }

    /** 更新页码指示器并重置自动隐藏计时器 */
    private fun updateIndicator() {
        delayedHide(3000)
        supportActionBar?.title = "${binding.galleryPager.currentItem + 1} / ${imageUrls.size}"
    }

    /** 切换工具栏显示/隐藏 */
    private fun toggle() {
        if (mVisible) hide() else {
            show()
            delayedHide(3000)
        }
    }

    /** 隐藏工具栏和系统栏 */
    private fun hide() {
        supportActionBar?.hide()
        binding.toolbarGallery.startAnimation(fadeOut)
        mVisible = false
        handler.removeCallbacks(showPart2Runnable)
        handler.postDelayed(hidePart2Runnable, 300)
    }

    /** 显示工具栏 */
    private fun show() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        mVisible = true
        handler.removeCallbacks(hidePart2Runnable)
        handler.postDelayed(showPart2Runnable, 300)
    }

    /** 延迟隐藏工具栏 */
    private fun delayedHide(delayMillis: Int) {
        handler.removeCallbacks(hideRunnable)
        handler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gallery, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            val m = movie ?: return super.onOptionsItemSelected(item)
            val dir = File(
                JAViewer.getStorageDir(this),
                "/movies/${m.code} ${m.title}".replace(Regex("[\\\\/:*?\"<>|\\[\\]]"), "-")
            )
            dir.mkdirs()
            val index = binding.galleryPager.currentItem
            Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrls[index])
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            BufferedOutputStream(FileOutputStream(File(dir, "${index + 1}.jpeg"))).use { os ->
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, os)
                            }
                            if (!isFinishing && !isDestroyed) {
                                Toast.makeText(this@GalleryActivity, "成功保存到 $dir", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            onLoadFailed(e)
                        }
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}

                    private fun onLoadFailed(e: Exception) {
                        if (!isFinishing && !isDestroyed) {
                            Toast.makeText(this@GalleryActivity, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /** ViewPager 图片适配器 */
    private inner class ImageAdapter(
        private val context: Context,
        private val urls: Array<String>
    ) : PagerAdapter() {

        override fun destroyItem(container: android.view.ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun getCount(): Int = urls.size

        override fun instantiateItem(container: android.view.ViewGroup, position: Int): Any {
            val binding = ContentGalleryBinding.inflate(layoutInflater, container, false)

            binding.image.setOnClickListener { toggle() }

            Glide.with(context.applicationContext)
                .load(urls[position])
                .into(object : CustomTarget<android.graphics.drawable.Drawable>() {
                    override fun onResourceReady(resource: android.graphics.drawable.Drawable, transition: Transition<in android.graphics.drawable.Drawable>?) {
                        binding.progressBar.visibility = View.GONE
                        binding.image.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}

                    override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        binding.galleryTextError.text = "图片加载失败 :("
                    }
                })

            container.addView(binding.root, 0)
            return binding.root
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    }
}
