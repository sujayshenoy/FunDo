package com.example.fundo.services

import android.util.Log
import com.example.fundo.models.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
}