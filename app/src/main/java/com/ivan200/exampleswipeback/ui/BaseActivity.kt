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

    private var activityFragmentManager: ActivityFragmentManager? = null
    val activeFragment: Fragment? get() = activityFragmentManager?.activeFragment
    val activeFragmentTag: String? get() = activityFragmentManager?.activeFragment?.tag
    fun updateActiveFragment() = activityFragmentManager?.updateActiveFragment()
    fun removeAllFragments() = activityFragmentManager?.removeAllFragments()
    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>) = setCurrentFragment(fragmentClass, null)
    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>, args: Bundle?) =
        tryResumeAction { activityFragmentManager?.setCurrentFragment(fragmentClass, args) }

    abstract val layoutId: Int

    var onResumeHandler: Function0<Unit>? = null

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        activityFragmentManager = ActivityFragmentManager(supportFragmentManager, R.id.content_frame)

        setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
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

    open fun showException(throwable: Throwable) {
        DialogHelper(this).withThrowable(throwable).show()
    }


    fun showError(throwable: Throwable) {
        tryResumeAction {
            runOnUiThread {
                DialogHelper(this).withThrowable(throwable).show()
            }
        }
    }

    fun onLastFragmentClose() {
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            onLastFragmentClose()
            return
        }
        //Utils.hideKeyboard(this)
        super.onBackPressed()
    }
}
