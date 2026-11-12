package com.deenbrowser.app.browser

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat

class NestedScrollWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.webViewStyle
) : WebView(context, attrs, defStyleAttr), NestedScrollingChild3 {

    private val childHelper = NestedScrollingChildHelper(this)
    private var lastMotionY = 0
    private val scrollOffset = IntArray(2)
    private val scrollConsumed = IntArray(2)

    init {
        isNestedScrollingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var returnValue = false
        val eventCopy = MotionEvent.obtain(event)
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_DOWN) {
            scrollOffset[0] = 0
            scrollOffset[1] = 0
        }

        val eventY = event.y.toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionY = eventY
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                returnValue = super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                var deltaY = lastMotionY - eventY
                val atTop = !canScrollVertically(-1)

                // Dispatch to parent Compose PullToRefresh when pulling down at top of page
                if (atTop && deltaY < 0) {
                    if (dispatchNestedPreScroll(0, deltaY, scrollConsumed, scrollOffset, ViewCompat.TYPE_TOUCH)) {
                        deltaY -= scrollConsumed[1]
                        eventCopy.offsetLocation(0f, scrollOffset[1].toFloat())
                    }
                    if (deltaY < 0) {
                        dispatchNestedScroll(0, 0, 0, deltaY, scrollOffset, ViewCompat.TYPE_TOUCH)
                    }
                }

                lastMotionY = eventY - scrollOffset[1]
                returnValue = super.onTouchEvent(eventCopy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
                returnValue = super.onTouchEvent(event)
            }
        }

        eventCopy.recycle()
        return returnValue
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }
}