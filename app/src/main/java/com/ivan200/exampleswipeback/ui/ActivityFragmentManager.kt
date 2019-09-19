package com.ivan200.exampleswipeback.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

//
// Created by Ivan200 on 16.09.2019.
//
class ActivityFragmentManager(
    var supportFragmentManager: FragmentManager,
    @IdRes var contentFragmentId: Int
) {
    private var allowUpdateActiveFragment = true
    private var blockOneFragmentUpdate = false

    val activeFragment: Fragment? get() = supportFragmentManager.findFragmentById(contentFragmentId)
    fun updateFragment(fragment: Fragment? = null) = (fragment ?: activeFragment)?.onResume()

    private fun createFragment(fragmentClass: Class<out BaseFragment>): BaseFragment {
        return supportFragmentManager.fragmentFactory
            .instantiate(ClassLoader.getSystemClassLoader(), fragmentClass.name) as BaseFragment
    }

    init {
        supportFragmentManager.addOnBackStackChangedListener {
            when {
                blockOneFragmentUpdate -> blockOneFragmentUpdate = false
                allowUpdateActiveFragment -> this@ActivityFragmentManager.updateFragment()
            }
        }
    }

    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>, args: Bundle? = null) {
        try {
            val fragment = supportFragmentManager.findFragmentByTag(fragmentClass.simpleName)
            if (fragment == null) {
                //Single blocking of the fragment update, because when the fragment is added,
                //the stack will be changed and backStackChangedListener will be triggered
                //To prevent double onResume
                blockOneFragmentUpdate = true
                createFragment(fragmentClass).let {
                    it.arguments = args
                    supportFragmentManager.beginTransaction()
                        .add(contentFragmentId, it, fragmentClass.simpleName)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .addToBackStack(fragmentClass.simpleName)
                        .commit()
                }
            } else {
                fragment.let {
                    it.arguments = args
                    if (it.tag == activeFragment?.tag) updateFragment(it)
                    else supportFragmentManager.popBackStackImmediate(it.tag, 0)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeAllFragments() {
        //Since the stack changes while we cleaning all the fragments, backStackChangedListener triggers
        //The switch is needed to prevent multiple calls update on closing fragments and crash app.
        allowUpdateActiveFragment = false
        supportFragmentManager.apply {
            beginTransaction().also { transition->
                fragments.forEach { transition.remove(it) }
            }.commit()

            while (backStackEntryCount > 0) {
                try {
                    popBackStackImmediate()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        allowUpdateActiveFragment = true
    }
}
