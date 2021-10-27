package com.example.fundo.services

import android.os.Bundle
import android.util.Log
import com.example.fundo.models.DBUser
import com.example.fundo.models.User
import com.example.fundo.utils.SharedPrefUtil
import com.example.fundo.utils.Utilities
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.security.auth.callback.Callback

object Database {
    private val database = Firebase.database.reference

    fun addUserToDB(user : User, callback : (Boolean) -> Unit){
        val userDB = DBUser(user.name,user.email,user.phone)
        database.child("users").child(Auth.getCurrentUser()?.uid.toString()).setValue(userDB).addOnCompleteListener {
            if(it.isSuccessful){
                Utilities.addUserToSharedPref(userDB)
                callback(true)
            }
            else{
                Log.e("DB","Write Failed")
                Log.e("DB",it.exception.toString())
                callback(false)
            }
        }
    }

    fun getUserFromDB(callback: (Boolean) -> Unit) {
        database.child("users").child(Auth.getCurrentUser()?.uid.toString()).get()
            .addOnCompleteListener { status ->
                if(status.isSuccessful) {
                    status.result.also {
                        Log.i("DB","User from DB $it")
                        val user = Utilities.createUserFromHashMap(it?.value as HashMap<*, *>)
                        Utilities.addUserToSharedPref(user)
                        callback(true)
                    }
                }
                else{
                    Log.e("DB","Read Failed")
                    Log.e("DB",status.exception.toString())
                    callback(false)
                }
        }
    }
}