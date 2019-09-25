package com.ivan200.exampleswipeback.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentOne : BaseFragment(R.layout.fragment_one) {

    override val title: Int get() = R.string.fr_one

    private val button1 get() = mView.findViewById<Button>(R.id.button1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button1.setOnClickListener {
            mActivity.setCurrentFragment(FragmentTwo::class.java)
        }
    }
}