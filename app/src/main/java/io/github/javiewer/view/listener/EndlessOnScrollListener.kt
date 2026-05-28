package io.github.javiewer.view.listener

abstract class EndlessOnScrollListener<I> : BasicOnScrollListener<I>() {
    override fun isEnd(): Boolean = false
}
