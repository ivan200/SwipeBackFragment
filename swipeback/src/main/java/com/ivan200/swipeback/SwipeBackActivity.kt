package com.ivan200.swipeback

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

open class SwipeBackActivity : AppCompatActivity {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    lateinit var swipeBackLayout: SwipeBackLayout
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onActivityCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        swipeBackLayout.attachToActivity(this)
    }

    override fun <T : View?> findViewById(id: Int): T {
        return super.findViewById<T>(id) ?: swipeBackLayout.findViewById<T>(id)
    }

    private fun onActivityCreate() {
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.background = null
        } else{
            window.decorView.setBackgroundDrawable(null)
        }
        swipeBackLayout = SwipeBackLayout(this)
        swipeBackLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    protected fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        swipeBackLayout.edgeLevel = edgeLevel
    }

    protected fun setEdgeLevel(widthPixel: Int) {
        swipeBackLayout.edgeLevelPixels = widthPixel
    }

    protected fun setPreDragPercent(percent: Float) {
        swipeBackLayout.preDragPercent = percent
    }

    protected fun setEdgeOrientation(orientation: SwipeBackLayout.SwipeOrientation) {
        swipeBackLayout.currentSwipeOrientation = orientation
    }

    fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    fun swipeBackPriority(): Boolean {
        return supportFragmentManager.backStackEntryCount <= 1
    }

    @DrawableRes
    var mDefaultFragmentBackground: Int = 0

}
