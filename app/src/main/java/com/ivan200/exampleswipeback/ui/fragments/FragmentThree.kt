package com.ivan200.exampleswipeback.ui.fragments

import android.view.View
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentThree : BaseFragment(R.layout.fragment_three) {
    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override val title: Int
        get() = R.string.fr_three

}