package com.ivan200.exampleswipeback.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ivan200.exampleswipeback.R
import com.ivan200.swipeback.SwipeBackActivity
import com.ivan200.swipeback.SwipeBackLayout

//
// Created by Ivan200 on 16.09.2019.
//
abstract class BaseActivity : SwipeBackActivity() {

    private var mManager: ActivityFragmentManager? = null
    val activeFragment: Fragment? get() = mManager?.activeFragment
    val activeFragmentTag: String? get() = mManager?.activeFragment?.tag
    fun updateActiveFragment() = mManager?.updateFragment()
    fun removeAllFragments() = mManager?.removeAllFragments()
    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>, args: Bundle? = null) = mManager?.setCurrentFragment(fragmentClass, args)

    abstract val layoutId: Int

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        if(mManager == null) {
            mManager = ActivityFragmentManager(supportFragmentManager, R.id.content_frame)
            setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
        }
    }

    open fun onLastFragmentClosed() {
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            onLastFragmentClosed()
            return
        }
        super.onBackPressed()
    }
}