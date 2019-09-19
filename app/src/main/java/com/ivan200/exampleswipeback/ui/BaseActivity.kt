package com.ivan200.exampleswipeback.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ivan200.exampleswipeback.R
import com.ivan200.swipeback.SwipeBackActivity
import com.ivan200.swipeback.SwipeBackLayout
import com.maxmoscow.erp.utils.DialogHelper

//
// Created by Ivan200 on 16.09.2019.
//
abstract class BaseActivity : SwipeBackActivity() {

    private var mManager: ActivityFragmentManager? = null
    val activeFragment: Fragment? get() = mManager?.activeFragment
    val activeFragmentTag: String? get() = mManager?.activeFragment?.tag
    fun updateActiveFragment() = mManager?.updateFragment()
    fun removeAllFragments() = mManager?.removeAllFragments()
    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>, args: Bundle? = null) =
        tryResumeAction { mManager?.setCurrentFragment(fragmentClass, args) }

    abstract val layoutId: Int

    var onResumeHandler: Function0<Unit>? = null

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

    override fun onResume() {
        super.onResume()

        onResumeHandler?.invoke()
        onResumeHandler = null
    }

    fun tryResumeAction(resumeHandler: Function0<Unit>) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            resumeHandler.invoke()
        } else {
            onResumeHandler = resumeHandler
        }
    }

    open fun showError(throwable: Throwable) {
        tryResumeAction {
            runOnUiThread {
                DialogHelper(this).withThrowable(throwable).show()
            }
        }
    }

    private fun onLastFragmentClosed() {
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