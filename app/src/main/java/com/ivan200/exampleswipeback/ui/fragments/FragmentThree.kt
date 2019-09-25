package com.ivan200.exampleswipeback.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.ivan200.exampleswipeback.R
import com.ivan200.exampleswipeback.ui.BaseFragment

//
// Created by Ivan200 on 16.09.2019.
//
class FragmentThree : BaseFragment(R.layout.fragment_three) {
    override val title: Int get() = R.string.fr_three

    private val list get() = mView.findViewById<ListView>(R.id.list)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val cellNames = IntRange(1,100).map { "Cell $it" }.toTypedArray()
        list.adapter = ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, cellNames)
    }
}