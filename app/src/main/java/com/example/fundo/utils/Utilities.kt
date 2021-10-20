package com.example.fundo.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.fundo.R

object Utilities {
    fun fragmentSwitcher(supportFragmentManager: FragmentManager,replaceFragment: Int, replaceWithfragment: Fragment) {
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(replaceFragment, replaceWithfragment)
        fragmentTransaction.commit()
    }
}