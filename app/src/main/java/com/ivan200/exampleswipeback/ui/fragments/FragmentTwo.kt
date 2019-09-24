package com.ivan200.exampleswipeback.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentTwo : BaseFragment(R.layout.fragment_two) {

    override val title: Int get() = R.string.fr_two

    private val button2 get() = mView.findViewById<Button>(R.id.button2)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        button2.setOnClickListener {
            mActivity.setCurrentFragment(FragmentThree::class.java)
        }
    }
}