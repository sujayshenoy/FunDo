package com.example.fundo.common

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.fundo.data.models.CloudDBUser

object Utilities {
    fun fragmentSwitcher(
        supportFragmentManager: FragmentManager, replaceFragment: Int,
        replaceWithFragment: Fragment
    ) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(replaceFragment, replaceWithFragment)
        fragmentTransaction.commit()
    }

    fun displayToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createUserFromHashMap(userMap: HashMap<*, *>): CloudDBUser {
        return CloudDBUser(
            userMap["name"].toString(), userMap["email"].toString(),
            userMap["phone"].toString()
        )
    }
}