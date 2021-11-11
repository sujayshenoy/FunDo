package com.example.fundo.common

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.fundo.data.models.DBUser

object Utilities {
    fun fragmentSwitcher(supportFragmentManager: FragmentManager, replaceFragment: Int,
                         replaceWithFragment: Fragment) {
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(replaceFragment, replaceWithFragment)
        fragmentTransaction.commit()
    }

    fun displayToast(context: Context, message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    fun createUserFromHashMap(userMap:HashMap<*,*>):DBUser {
        return DBUser(userMap["name"].toString(),userMap["email"].toString(),
            userMap["phone"].toString())
    }

    fun addUserToSharedPref(userDB: DBUser) {
        SharedPrefUtil.addString("userEmail",userDB.email)
        SharedPrefUtil.addString("userName",userDB.name)
        SharedPrefUtil.addString("userPhone",userDB.phone)
    }
}