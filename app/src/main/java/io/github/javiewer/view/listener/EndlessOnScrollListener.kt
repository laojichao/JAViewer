package io.github.javiewer.view.listener

import kotlinx.coroutines.CoroutineScope

/**
 * 无限滚动分页监听器，继承自 [BasicOnScrollListener]。
 *
 * 覆盖 [isEnd] 始终返回 false，适用于没有明确结束边界的数据源
 * （如 AVMOO 主站的分页列表）。
 *
 * @param I 列表项数据类型
 * @param scope 协程作用域
 */
abstract class EndlessOnScrollListener<I>(
    scope: CoroutineScope
) : BasicOnScrollListener<I>(scope) {
    override fun isEnd(): Boolean = false
}
