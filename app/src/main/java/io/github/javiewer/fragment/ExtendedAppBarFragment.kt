package io.github.javiewer.fragment

import androidx.fragment.app.Fragment

/**
 * 扩展 AppBar 的 Fragment 基类。
 *
 * 用于需要自定义 AppBar 行为的 Fragment（如类别页、收藏夹页），
 * MainActivity 通过判断 Fragment 是否继承此类来调整 AppBar 阴影高度。
 */
open class ExtendedAppBarFragment : Fragment()
