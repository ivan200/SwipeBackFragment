package com.ivan200.swipeback

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


//
// Created by Ivan200 on 16.09.2019.
//
open class SwipeBackFragment : Fragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    var swipeBackLayout: SwipeBackLayout? = null
        private set

    private var mNoAnim: Animation? = null

    internal var mLocking = false

    private val windowBackground: Int
        get() {
            val a =
                activity!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
            val background = a.getResourceId(0, 0)
            a.recycle()
            return background
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val isSupportHidden =
                savedInstanceState.getBoolean(SWIPEBACKFRAGMENT_STATE_SAVE_IS_HIDDEN)

            val ft = fragmentManager!!.beginTransaction()
            if (isSupportHidden) {
                ft.hide(this)
            } else {
                ft.show(this)
            }
            ft.commit()
        }

        mNoAnim = AnimationUtils.loadAnimation(activity, R.anim.no_anim)
        onFragmentCreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SWIPEBACKFRAGMENT_STATE_SAVE_IS_HIDDEN, isHidden)
    }

    private fun onFragmentCreate() {
        swipeBackLayout = SwipeBackLayout(activity!!)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        swipeBackLayout!!.layoutParams = params
        swipeBackLayout!!.setBackgroundColor(Color.TRANSPARENT)
    }

    protected fun attachToSwipeBack(view: View): View {
        return swipeBackLayout!!.also {
            it.attachToFragment(this, view)
        }
    }

    protected fun attachToSwipeBack(view: View, edgeLevel: SwipeBackLayout.EdgeLevel): View {
        return swipeBackLayout!!.also {
            it.attachToFragment(this, view)
            it.edgeLevel = edgeLevel
        }
    }

    protected fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        swipeBackLayout!!.edgeLevel = edgeLevel
    }

    protected fun setEdgeLevel(widthPixel: Int) {
        swipeBackLayout!!.setEdgeLevel(widthPixel)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden && swipeBackLayout != null) {
            swipeBackLayout!!.hiddenFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val view = view
        initFragmentBackground(view)
        if (view != null) {
            view.isClickable = true
        }
    }

    private fun initFragmentBackground(view: View?) {
        if (view is SwipeBackLayout) {
            val childView = view.getChildAt(0)
            setBackground(childView)
        } else {
            setBackground(view)
        }
    }

    private fun setBackground(view: View?) {
        if (view != null && view.background == null) {
            val defaultBg = (activity as? SwipeBackActivity)?.mDefaultFragmentBackground ?: 0
            view.setBackgroundResource(if (defaultBg == 0) windowBackground else defaultBg)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (mLocking) {
            mNoAnim
        } else super.onCreateAnimation(transit, enter, nextAnim)
    }

    fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

    companion object {
        private val SWIPEBACKFRAGMENT_STATE_SAVE_IS_HIDDEN =
            "SWIPEBACKFRAGMENT_STATE_SAVE_IS_HIDDEN"
    }
}
