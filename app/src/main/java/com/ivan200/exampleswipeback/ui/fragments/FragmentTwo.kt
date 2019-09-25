package com.ivan200.exampleswipeback.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.customview.widget.ViewDragHelper
import com.ivan200.exampleswipeback.MainActivity
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentTwo : BaseFragment(R.layout.fragment_two) {

    override val title: Int get() = R.string.fr_two

    private val button2 get() = mView.findViewById<Button>(R.id.button2)

    private val checkLeft get() = mView.findViewById<CheckBox>(R.id.check_left)
    private val checkTop get() = mView.findViewById<CheckBox>(R.id.check_top)
    private val checkRight get() = mView.findViewById<CheckBox>(R.id.check_right)
    private val checkBottom get() = mView.findViewById<CheckBox>(R.id.check_bottom)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        button2.setOnClickListener {
            mActivity.setCurrentFragment(FragmentThree::class.java)
        }

        checkLeft.setOnCheckedChangeListener (this::setSwipeDirection)
        checkTop.setOnCheckedChangeListener (this::setSwipeDirection)
        checkRight.setOnCheckedChangeListener (this::setSwipeDirection)
        checkBottom.setOnCheckedChangeListener (this::setSwipeDirection)

        restoreSwipeDirection((mActivity as MainActivity).swipeDirection)
    }

    fun restoreSwipeDirection(direction: Int) {
        setCheckedNoAnim(checkLeft, direction and ViewDragHelper.EDGE_LEFT != 0)
        setCheckedNoAnim(checkTop, direction and ViewDragHelper.EDGE_TOP != 0)
        setCheckedNoAnim(checkRight, direction and ViewDragHelper.EDGE_RIGHT != 0)
        setCheckedNoAnim(checkBottom, direction and ViewDragHelper.EDGE_BOTTOM != 0)
    }

    fun setCheckedNoAnim(compoundButton: CompoundButton, isChecked: Boolean){
        compoundButton.isChecked = isChecked
        compoundButton.jumpDrawablesToCurrentState()
    }

    fun setSwipeDirection(buttonView: CompoundButton, isChecked: Boolean){
        var swipe = 0

        if(checkLeft.isChecked) swipe = swipe or ViewDragHelper.EDGE_LEFT
        if(checkTop.isChecked) swipe = swipe or ViewDragHelper.EDGE_TOP
        if(checkRight.isChecked) swipe = swipe or ViewDragHelper.EDGE_RIGHT
        if(checkBottom.isChecked) swipe = swipe or ViewDragHelper.EDGE_BOTTOM

        setEdgeOrientation(swipe)
        (mActivity as MainActivity).swipeDirection = swipe
    }

}