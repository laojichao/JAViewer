package io.github.javiewer.view.listener

import kotlinx.coroutines.CoroutineScope

abstract class EndlessOnScrollListener<I>(
    scope: CoroutineScope
) : BasicOnScrollListener<I>(scope) {
    override fun isEnd(): Boolean = false
}
