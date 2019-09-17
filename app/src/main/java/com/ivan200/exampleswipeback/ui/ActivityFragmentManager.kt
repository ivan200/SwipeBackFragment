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
    fun updateActiveFragment() = activeFragment?.onResume()

    private fun createFragment(fragmentClass: Class<out BaseFragment>) : BaseFragment {
        return supportFragmentManager.fragmentFactory.instantiate(ClassLoader.getSystemClassLoader(), fragmentClass.name) as BaseFragment
    }

    init {
        supportFragmentManager.addOnBackStackChangedListener {
            when {
                blockOneFragmentUpdate -> blockOneFragmentUpdate = false
                allowUpdateActiveFragment -> this@ActivityFragmentManager.updateActiveFragment()
            }
        }
    }

    fun setCurrentFragment(fragmentClass: Class<out BaseFragment>, args: Bundle?) {
        try {
            val fragment = supportFragmentManager.findFragmentByTag(fragmentClass.simpleName)

            if (fragment == null) {
                //одиночная блокировка обновления фрагмента, так как при добавлении фрагмента изменится стек и сработает backStackChangedListener
                //для предотвращения двойного onResume
                blockOneFragmentUpdate = true
                val fDataNewInstance = createFragment(fragmentClass)
                fDataNewInstance.arguments = args
                fDataNewInstance.activityFragmentManager = this
                supportFragmentManager.beginTransaction()
                    .add(contentFragmentId, fDataNewInstance, fragmentClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .addToBackStack(fragmentClass.simpleName)
                    .commit()
            } else {
                val activeFragment = activeFragment
                fragment.arguments = args
                if (activeFragment == null || activeFragment.tag == null || activeFragment.tag != fragment.tag) {
                    supportFragmentManager.popBackStackImmediate(fragment.tag, 0)
                } else {
                    fragment.onResume()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeAllFragments() {
        //так как при очистке всех фрагментов изменяется стек, срабатывает backStackChangedListener
        //флаг нужен для предотвращения множественного вызова onResume на закрывающихся фрагментах, и краше приложения
        allowUpdateActiveFragment = false

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        for (activeFragment in supportFragmentManager.fragments) {
            fragmentTransaction.remove(activeFragment)
        }
        fragmentTransaction.commit()

        while (supportFragmentManager.backStackEntryCount > 0) {
            try {
                supportFragmentManager.popBackStackImmediate()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        allowUpdateActiveFragment = true
    }
}
