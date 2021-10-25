package com.example.fundo.services

import android.util.Log
import com.example.fundo.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.security.auth.callback.Callback

object Database {
    private val database = Firebase.database.reference

    fun addUserToDB(user : User){
        database.child("users").child(Auth.getCurrentUser()?.uid.toString()).setValue(user).addOnCompleteListener {
            if(!it.isSuccessful){
                Log.e("DB","Write Failed")
                Log.e("DB",it.exception.toString())
            }
        }
    }

    fun getUserFromDB(callback: (HashMap<*,*>) -> Unit) {
        database.child("users").child(Auth.getCurrentUser()?.uid.toString()).get()
            .addOnCompleteListener { status ->
                if(!status.isSuccessful) {
                    Log.e("DB","Read Failed")
                    Log.e("DB",status.exception.toString())
                }
                else{
                    status.result.also {
                        Log.i("DB","User from DB $it")
                        callback(it?.value as HashMap<*,*>)
                    }
                }
        }
    }
}