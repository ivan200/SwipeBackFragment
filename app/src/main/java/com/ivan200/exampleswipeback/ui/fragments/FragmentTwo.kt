package com.ivan200.exampleswipeback.ui.fragments

import android.view.View
import android.widget.Button
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentTwo : BaseFragment(R.layout.fragment_two) {

    private val button2 by lazy { mView.findViewById<Button>(R.id.button2) }

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        button2.setOnClickListener {
            mActivity.setCurrentFragment(FragmentThree::class.java)
        }
    }

    override val title: Int
        get() = R.string.fr_two

}