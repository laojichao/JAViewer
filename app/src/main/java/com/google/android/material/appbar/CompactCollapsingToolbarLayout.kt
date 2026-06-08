package com.google.android.material.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CompactCollapsingToolbarLayout : CollapsingToolbarLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hms = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, hms)
        val mode = MeasureSpec.getMode(hms)
        val topInset = ViewCompat.getRootWindowInsets(this)
            ?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
        if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
            hms = MeasureSpec.makeMeasureSpec(measuredHeight - topInset, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, hms)
        }
    }
}
