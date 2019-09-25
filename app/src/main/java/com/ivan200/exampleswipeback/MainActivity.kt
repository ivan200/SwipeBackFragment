package com.ivan200.exampleswipeback

import android.os.Bundle
import androidx.customview.widget.ViewDragHelper
import com.ivan200.exampleswipeback.ui.BaseActivity
import com.ivan200.exampleswipeback.ui.fragments.FragmentOne

//
// Created by Ivan200 on 16.09.2019.
//
class MainActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.main_activity

    var swipeDirection = ViewDragHelper.EDGE_LEFT
        set(value) {
            field = value
            setEdgeOrientation(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(activeFragmentTag.isNullOrEmpty()) {
            setCurrentFragment(FragmentOne::class.java)
        }

        swipeDirection = ViewDragHelper.EDGE_LEFT
    }
}
