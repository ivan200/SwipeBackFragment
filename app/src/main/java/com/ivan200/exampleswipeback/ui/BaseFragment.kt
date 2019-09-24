package com.ivan200.exampleswipeback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.ivan200.exampleswipeback.R
import com.ivan200.swipeback.SwipeBackFragment
import com.ivan200.swipeback.SwipeBackLayout

//
// Created by Ivan200 on 16.09.2019.
//
abstract class BaseFragment(contentLayoutId: Int) : SwipeBackFragment(contentLayoutId){
    @StringRes
    open val title = R.string.app_name
    open val titleString : String? = null

    val mActivity  get() = activity as BaseActivity
    val toolbar get() = mView.findViewById<Toolbar?>(R.id.toolbar)

    lateinit var mView: View
    private lateinit var swipeView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = super.onCreateView(inflater, container, savedInstanceState)!!

        toolbar?.let {
            mActivity.setSupportActionBar(it)
            mActivity.title = titleString ?: getString(title)
            it.setNavigationOnClickListener { mActivity.onBackPressed() }
        }

        swipeView = attachToSwipeBack(mView)
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
        return swipeView
    }

}