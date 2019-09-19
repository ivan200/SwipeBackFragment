package com.ivan200.exampleswipeback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.ivan200.exampleswipeback.R
import com.ivan200.swipeback.SwipeBackFragment
import com.ivan200.swipeback.SwipeBackLayout

//
// Created by Ivan200 on 16.09.2019.
//
abstract class BaseFragment(contentLayoutId: Int) : SwipeBackFragment(contentLayoutId){
    abstract val title: Int

    val mActivity  by lazy { activity as BaseActivity }
    val toolbar by lazy { mView.findViewById<Toolbar?>(R.id.toolbar) }
    lateinit var mView: View
    lateinit var swipeView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(!::mView.isInitialized){
            mView = super.onCreateView(inflater, container, savedInstanceState)!!

            toolbar?.let {
                mActivity.setSupportActionBar(it)
                mActivity.title = getText(title)
                it.setNavigationOnClickListener { mActivity.onBackPressed() }
            }

            initialize(mView)
            swipeView = attachToSwipeBack(mView)
            setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
        }
        return swipeView
    }

    abstract fun initialize(view: View)
}