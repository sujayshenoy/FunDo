package com.example.fundo.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.fundo.models.User

object Utilities {
    fun fragmentSwitcher(supportFragmentManager: FragmentManager,replaceFragment: Int, replaceWithfragment: Fragment) {
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(replaceFragment, replaceWithfragment)
        fragmentTransaction.commit()
    }

    fun displayToast(context: Context, message:String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    fun createUserFromHashMap(userMap:HashMap<*,*>):User {
        return User(userMap["name"].toString(),userMap["email"].toString(),userMap["phone"].toString())
    }
}