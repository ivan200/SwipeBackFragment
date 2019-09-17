package com.ivan200.exampleswipeback.ui.fragments

import android.view.View
import android.widget.Button
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentOne : BaseFragment(R.layout.fragment_one) {

    private val button1 by lazy { mView.findViewById<Button>(R.id.button1) }

    override fun initialize(view: View) {
        button1.setOnClickListener {
            mActivity.setCurrentFragment(FragmentTwo::class.java)
        }
    }

    override val title: Int
        get() = R.string.fr_one

}