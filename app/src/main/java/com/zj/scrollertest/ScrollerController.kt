package com.zj.scrollertest

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.core.animation.addListener
import kotlin.math.abs
import kotlin.math.max

@Suppress("MemberVisibilityCanBePrivate")
abstract class ScrollerController<C> @JvmOverloads constructor(c: Context, attr: AttributeSet? = null, def: Int = 0) : FrameLayout(c, attr, def) {
    private var mostHeight = 0
    private var scrolled = 0f
    private var parseCancel = false
    private var inAnimation = false
    private val leastHeight = 260f
    private var offsetListener: ((lh: Int, leastHeight: Int, mostHeight: Int, pl: Any?) -> Unit)? = null
    var isExpand = true
    var isFold = false
    var isFullScreenInit = true

    fun setOnOffsetListener(offsetListener: (lh: Int, leastHeight: Int, mostHeight: Int, pl: Any?) -> Unit) {
        this.offsetListener = offsetListener
    }

    open fun onExpandChanged(isExpand: Boolean, isFold: Boolean, cur: Int, least: Int, most: Int) {}

//    override fun onFullMaxScreenChanged(isFull: Boolean, fromFocusChange: Boolean) {
//        super.onFullMaxScreenChanged(isFull, fromFocusChange)
//        isFullScreenInit = !isExpand && isExpand
//    }
//
//    override fun onFullScreenChanged(isFull: Boolean, payloads: Map<String, Any?>?) {
//        super.onFullScreenChanged(isFull, payloads)
//        if (isFull && payloads?.get("fold") != null) {
//            isFullScreenInit = false
//            post {
//                scrollToComment(payloads, 0)
//                isFold = true
//                isExpand = false
//            }
//        } else {
//            isFullScreenInit = true
//            if (!isFull) {
//                isFold = false
//                isExpand = false
//                viewScroller?.clear()
//                scrolled = 0f
//                mostHeight = 0
//            }
//        }
//    }
//
//    fun scrollToComment(pl: Any? = null, duration: Long = 250) {
//        if (inAnimation || isFold) return
//        viewScroller?.clear()
//        videoRoot?.let {
//            if (duration <= 0) {
//                scrolled = max(it.height, mostHeight).toFloat()
//                mostHeight = max(it.height, mostHeight)
//                onMoving(it, mostHeight, TrackOrientation.BOTTOM_TOP, pl)
//            } else {
//                inAnimation = true
//                it.post {
//                    scrolled = max(it.height, mostHeight).toFloat()
//                    mostHeight = max(it.height, mostHeight)
//                    val anim = ValueAnimator.ofFloat(0.0f, 1.0f)
//                    anim.addUpdateListener { a ->
//                        onMoving(it, (mostHeight * a.animatedFraction).toInt(), TrackOrientation.BOTTOM_TOP, pl)
//                    }
//                    anim.addListener(onEnd = {
//                        inAnimation = false
//                    })
//                    anim.duration = duration
//                    anim.start()
//                }
//            }
//        }
//    }
//
//    @CallSuper
//    override fun onFullKeyEvent(code: Int, event: KeyEvent): Boolean {
//        return if (inAnimation) true else super.onFullKeyEvent(code, event)
//    }
//
//    override fun onTouchActionEvent(videoRoot: View?, event: MotionEvent, lastX: Float, lastY: Float, orientation: TrackOrientation?): Boolean {
//        if (inAnimation) return true
//        if (lastY <= 0) return false
//        if (viewScroller == null) instanceScroller(videoRoot)
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                if (mostHeight == 0) mostHeight = videoRoot?.height ?: 0
//                viewScroller?.onEventDown(event)
//            }
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                parseCancel = false
//                if (parseCancel) mostHeight = 0
//                viewScroller?.onEventUp(event)
//                val isInterrupt = abs(scrolled) >= 20
//                if (isInterrupt) onTrack(isPlayable, start = false, end = true, formTrigDuration = 0f)
//                return isInterrupt
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (mostHeight == 0) return false
//                viewScroller?.onEventMove(event, lastY)
//                return onMoving(videoRoot, (lastY - event.rawY).toInt(), orientation)
//            }
//        }
//        return false
//    }
//
//    private fun onMoving(videoRoot: View?, dy: Int, orientation: TrackOrientation?, pl: Any? = null): Boolean {
//        videoRoot?.let {
//            return if (scrolled == 0.0f && orientation == TrackOrientation.TOP_BOTTOM) {
//                parseCancel = true
//                setLayoutParams(it, ViewGroup.LayoutParams.MATCH_PARENT)
//                viewScroller?.clear()
//                false
//            } else if (parseCancel) {
//                if (scrolled == 0f && orientation != TrackOrientation.TOP_BOTTOM) parseCancel = it.layoutParams.height < mostHeight
//                viewScroller?.clear()
//                return false
//            } else {
//                var lh = 0
//                try {
//                    var lph = it.layoutParams.height
//                    if (lph == ViewGroup.LayoutParams.MATCH_PARENT) lph = mostHeight
//                    lh = (lph - dy).coerceAtLeast(leastHeight).coerceAtMost(mostHeight)
//                    setLayoutParams(it, lh)
//                    true
//                } finally {
//                    if (lh == leastHeight || lh == mostHeight) {
//                        if (lh == mostHeight) {
//                            scrolled = 0f
//                            isExpand = true
//                        }
//                        if (lh == leastHeight) {
//                            scrolled = mostHeight.toFloat()
//                            isFold = true
//                        }
//                        viewScroller?.clear()
//                    } else {
//                        scrolled += dy
//                        isFold = false
//                        isExpand = false
//                    }
//                    offsetListener?.invoke(lh, leastHeight, mostHeight, pl)
//                    onExpandChanged(isExpand, isFold, lh, leastHeight, mostHeight)
//                }
//            }
//        }
//        return false
//    }
//
//    private fun setLayoutParams(videoRoot: View, h: Int) {
//        val height = if (h == ViewGroup.LayoutParams.MATCH_PARENT) h else if (h >= mostHeight) ViewGroup.LayoutParams.MATCH_PARENT else h
//        val lp = videoRoot.layoutParams
//        lp.height = height
//        videoRoot.layoutParams = lp
//        (videoRoot.parent as? ViewGroup)?.let { vp ->
//            val lpp = vp.layoutParams
//            lpp.height = height
//            vp.layoutParams = lpp
//        }
//    }
//
//    private fun instanceScroller(videoRoot: View?) {
//        viewScroller = object : ViewScroller(videoRoot ?: this@ScrollerController.videoRoot ?: this@ScrollerController) {
//            override fun constrainScrollBy(dx: Int, dy: Int) {
//                val or = if (dy > 0) TrackOrientation.TOP_BOTTOM else TrackOrientation.BOTTOM_TOP
//                onMoving(videoRoot, dy, or)
//            }
//        }
//    }
}