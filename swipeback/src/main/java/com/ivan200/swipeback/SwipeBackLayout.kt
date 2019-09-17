package com.ivan200.swipeback

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.math.max
import kotlin.math.min


//
// Created by Ivan200 on 16.09.2019.
//

open class SwipeBackLayout : FrameLayout {
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    private var mScrollFinishThreshold: Float = DEFAULT_SCROLL_THRESHOLD

    private var mHelper: ViewDragHelper? = null

    private var mScrollPercent: Float = 0f
    private var mScrimOpacity: Float = 0f

    private var mActivity: FragmentActivity? = null
    private var mContentView: View? = null
    private var mFragment: SwipeBackFragment? = null
    private var mPreFragment: Fragment? = null

    private var mShadowLeft: Drawable? = null
    private var mShadowRight: Drawable? = null
    private val mTmpRect: Rect = Rect()

    private var mEdgeFlag: Int = 0
    private var mEnable = true
    private var mCurrentSwipeOrientation: Int = 0
    var edgeLevel: EdgeLevel? = null
        set(edgeLevel) {
            field = edgeLevel
            validateEdgeLevel(0, edgeLevel)
        }

    /**
     * The set of listeners to be sent events through.
     */
    private var mListeners: MutableList<OnSwipeListener>? = null

    enum class EdgeLevel {
        MAX, MIN, MED
    }

    private fun init() {
        mHelper = ViewDragHelper.create(this, ViewDragCallback())
        setShadow(R.drawable.shadow_left, EDGE_LEFT)
        setEdgeOrientation(EDGE_LEFT)
    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     *
     * @param threshold
     */
    fun setScrollThresHold(threshold: Float) {
        require(!(threshold >= 1.0f || threshold <= 0)) { "Threshold value should be between 0 and 1.0" }
        mScrollFinishThreshold = threshold
    }

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's [ViewDragHelper.Callback.onEdgeTouched] and
     * [ViewDragHelper.Callback.onEdgeDragStarted] methods will only be invoked
     * for edges for which edge tracking has been enabled.
     *
     * @param orientation Combination of edge flags describing the edges to watch
     * @see .EDGE_LEFT
     *
     * @see .EDGE_RIGHT
     */
    fun setEdgeOrientation(orientation: Int) {
        mEdgeFlag = orientation
        mHelper!!.setEdgeTrackingEnabled(orientation)

        if (orientation == EDGE_RIGHT || orientation == EDGE_ALL) {
            setShadow(R.drawable.shadow_right, EDGE_RIGHT)
        }
    }

    fun setEdgeLevel(widthPixel: Int) {
        validateEdgeLevel(widthPixel, null)
    }

    private fun validateEdgeLevel(widthPixel: Int, edgeLevel: EdgeLevel?) {
        try {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)
            val mEdgeSize = mHelper!!.javaClass.getDeclaredField("mEdgeSize")
            mEdgeSize.isAccessible = true
            if (widthPixel != 0) {
                mEdgeSize.setInt(mHelper, widthPixel)
            } else {
                when (edgeLevel) {
                    EdgeLevel.MAX -> mEdgeSize.setInt(mHelper, metrics.widthPixels)
                    EdgeLevel.MED -> mEdgeSize.setInt(mHelper, metrics.widthPixels / 2)
                    EdgeLevel.MIN -> mEdgeSize.setInt(mHelper, (20 * metrics.density + 0.5f).toInt())
                }
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    @IntDef(EDGE_LEFT, EDGE_RIGHT, EDGE_ALL)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class EdgeOrientation

    /**
     * Set a drawable used for edge shadow.
     */
    fun setShadow(shadow: Drawable, edgeFlag: Int) {
        if (edgeFlag and EDGE_LEFT != 0) {
            mShadowLeft = shadow
        } else if (edgeFlag and EDGE_RIGHT != 0) {
            mShadowRight = shadow
        }
        invalidate()
    }

    /**
     * Set a drawable used for edge shadow.
     */
    fun setShadow(resId: Int, edgeFlag: Int) {
        setShadow(resources.getDrawable(resId), edgeFlag)
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    fun addSwipeListener(listener: OnSwipeListener) {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        mListeners!!.add(listener)
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    fun removeSwipeListener(listener: OnSwipeListener) {
        if (mListeners == null) {
            return
        }
        mListeners!!.remove(listener)
    }

    interface OnSwipeListener {
        /**
         * Invoke when state change
         *
         * @param state flag to describe scroll state
         * @see .STATE_IDLE
         *
         * @see .STATE_DRAGGING
         *
         * @see .STATE_SETTLING
         */
        fun onDragStateChange(state: Int)

        /**
         * Invoke when edge touched
         *
         * @param oritentationEdgeFlag edge flag describing the edge being touched
         * @see .EDGE_LEFT
         *
         * @see .EDGE_RIGHT
         */
        fun onEdgeTouch(oritentationEdgeFlag: Int)

        /**
         * Invoke when scroll percent over the threshold for the first time
         *
         * @param scrollPercent scroll percent of this view
         */
        fun onDragScrolled(scrollPercent: Float)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val isDrawView = child === mContentView
        val drawChild = super.drawChild(canvas, child, drawingTime)
        if (isDrawView && mScrimOpacity > 0 && mHelper!!.viewDragState != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child)
            drawScrim(canvas, child)
        }
        return drawChild
    }

    private fun drawShadow(canvas: Canvas, child: View) {
        val childRect = mTmpRect
        child.getHitRect(childRect)

        if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
            mShadowLeft!!.setBounds(
                childRect.left - mShadowLeft!!.intrinsicWidth,
                childRect.top,
                childRect.left,
                childRect.bottom
            )
            mShadowLeft!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowLeft!!.draw(canvas)
        } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
            mShadowRight!!.setBounds(
                childRect.right,
                childRect.top,
                childRect.right + mShadowRight!!.intrinsicWidth,
                childRect.bottom
            )
            mShadowRight!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowRight!!.draw(canvas)
        }
    }

    private fun drawScrim(canvas: Canvas, child: View) {
        val baseAlpha = (DEFAULT_SCRIM_COLOR and -0x1000000).ushr(24)
        val alpha = (baseAlpha * mScrimOpacity).toInt()
        val color = alpha shl 24

        if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
            canvas.clipRect(0, 0, child.left, height)
        } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
            canvas.clipRect(child.right, 0, right, height)
        }
        canvas.drawColor(color)
    }

    override fun computeScroll() {
        mScrimOpacity = 1 - mScrollPercent
        if (mScrimOpacity >= 0) {
            if (mHelper!!.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    fun setFragment(fragment: SwipeBackFragment, view: View) {
        this.mFragment = fragment
        mContentView = view
    }

    fun hiddenFragment() {
        if (mPreFragment != null && mPreFragment!!.view != null) {
            mPreFragment!!.view!!.visibility = View.GONE
        }
    }

    fun attachToActivity(activity: FragmentActivity) {
        mActivity = activity
        val a = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()

        val decor = activity.window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decorChild.setBackgroundResource(background)
        decor.removeView(decorChild)
        addView(decorChild)
        setContentView(decorChild)
        decor.addView(this)
    }

    fun attachToFragment(swipeBackFragment: SwipeBackFragment, view: View) {
        addView(view)
        setFragment(swipeBackFragment, view)
    }

    private fun setContentView(view: View) {
        mContentView = view
    }

    fun setEnableGesture(enable: Boolean) {
        mEnable = enable
    }

    internal inner class ViewDragCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val dragEnable = mHelper!!.isEdgeTouched(mEdgeFlag, pointerId)
            if (dragEnable) {
                if (mHelper!!.isEdgeTouched(EDGE_LEFT, pointerId)) {
                    mCurrentSwipeOrientation = EDGE_LEFT
                } else if (mHelper!!.isEdgeTouched(EDGE_RIGHT, pointerId)) {
                    mCurrentSwipeOrientation = EDGE_RIGHT
                }

                if (mListeners != null && !mListeners!!.isEmpty()) {
                    for (listener in mListeners!!) {
                        listener.onEdgeTouch(mCurrentSwipeOrientation)
                    }
                }

                if (mPreFragment == null) {
                    if (mFragment != null) {
                        val fragmentList = mFragment!!.fragmentManager?.fragments
                        if (fragmentList != null && fragmentList.size > 1) {
                            val index = fragmentList.indexOf(mFragment)
                            for (i in index - 1 downTo 0) {
                                val fragment = fragmentList[i]
                                if (fragment != null && fragment.view != null) {
                                    fragment.view!!.visibility = View.VISIBLE
                                    mPreFragment = fragment
                                    break
                                }
                            }
                        } else {
                            return false
                        }
                    }
                } else {
                    val preView = mPreFragment!!.view
                    if (preView != null && preView.visibility != View.VISIBLE) {
                        preView.visibility = View.VISIBLE
                    }
                }
            }
            return dragEnable
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var ret = 0
            if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
                ret = min(child.width, max(left, 0))
            } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
                ret = min(0, max(left, -child.width))
            }
            return ret
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (width + mShadowLeft!!.intrinsicWidth))
            } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
                mScrollPercent =
                    Math.abs(left.toFloat() / (mContentView!!.width + mShadowRight!!.intrinsicWidth))
            }
            invalidate()

            if (mListeners != null && !mListeners!!.isEmpty()
                && mHelper!!.viewDragState == STATE_DRAGGING && mScrollPercent <= 1 && mScrollPercent > 0
            ) {
                for (listener in mListeners!!) {
                    listener.onDragScrolled(mScrollPercent)
                }
            }

            if (mScrollPercent > 1) {
                if (mFragment != null) {
                    if (mPreFragment is SwipeBackFragment) {
                        (mPreFragment as SwipeBackFragment).mLocking = true
                    }
                    if (!mFragment!!.isDetached) {
                        mFragment!!.mLocking = true
                        mFragment!!.fragmentManager!!.popBackStackImmediate()
                        mFragment!!.mLocking = false
                    }
                    if (mPreFragment is SwipeBackFragment) {
                        (mPreFragment as SwipeBackFragment).mLocking = false
                    }
                } else {
                    if (!mActivity!!.isFinishing) {
                        mActivity!!.finish()
                        mActivity!!.overridePendingTransition(0, 0)
                    }
                }
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            if (mFragment != null) {
                return 1
            } else if (mActivity != null && (mActivity as SwipeBackActivity).swipeBackPriority()) {
                return 1
            }
            return 0
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width

            val top = 0
            val left = when {
                (mCurrentSwipeOrientation and EDGE_LEFT != 0) && (xvel > 0 || xvel == 0f && mScrollPercent > mScrollFinishThreshold) -> {
                    childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
                }
                (mCurrentSwipeOrientation and EDGE_RIGHT != 0) && (xvel < 0 || xvel == 0f && mScrollPercent > mScrollFinishThreshold) -> {
                    -(childWidth + mShadowRight!!.intrinsicWidth + OVERSCROLL_DISTANCE)
                }
                else -> 0
            }

            mHelper!!.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            if (mListeners != null && !mListeners!!.isEmpty()) {
                for (listener in mListeners!!) {
                    listener.onDragStateChange(state)
                }
            }
        }

        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            super.onEdgeTouched(edgeFlags, pointerId)
            if (mEdgeFlag and edgeFlags != 0) {
                mCurrentSwipeOrientation = edgeFlags
            }
        }

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!mEnable) super.onInterceptTouchEvent(ev) else mHelper!!.shouldInterceptTouchEvent(
            ev
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mEnable) return super.onTouchEvent(event)
        mHelper!!.processTouchEvent(event)
        return true
    }

    companion object {
        /**
         * Edge flag indicating that the left edge should be affected.
         */
        const val EDGE_LEFT = ViewDragHelper.EDGE_LEFT

        /**
         * Edge flag indicating that the right edge should be affected.
         */
        const val EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT

        const val EDGE_ALL = EDGE_LEFT or EDGE_RIGHT


        /**
         * A view is not currently being dragged or animating as a result of a
         * fling/snap.
         */
        val STATE_IDLE = ViewDragHelper.STATE_IDLE

        /**
         * A view is currently being dragged. The position is currently changing as
         * a result of user input or simulated user input.
         */
        val STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING

        /**
         * A view is currently settling into place as a result of a fling or
         * predefined non-interactive motion.
         */
        val STATE_SETTLING = ViewDragHelper.STATE_SETTLING

        private val DEFAULT_SCRIM_COLOR = -0x67000000
        private val FULL_ALPHA = 255
        private val DEFAULT_SCROLL_THRESHOLD = 0.4f
        private val OVERSCROLL_DISTANCE = 10
    }
}