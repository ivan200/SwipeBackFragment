package com.ivan200.exampleswipeback

import android.os.Bundle
import com.ivan200.exampleswipeback.ui.BaseActivity
import com.ivan200.exampleswipeback.ui.fragments.FragmentOne

//
// Created by Ivan200 on 16.09.2019.
//
class MainActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCurrentFragment(FragmentOne::class.java)
    }
}
