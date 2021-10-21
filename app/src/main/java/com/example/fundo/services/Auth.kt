package com.example.fundo.services

import android.util.Log
import android.widget.Toast
import com.example.fundo.models.User
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object Auth {
    private val auth : FirebaseAuth = Firebase.auth

    fun getCurrentUser() = auth.currentUser //null if no user signed in

    fun signUpWithEmailAndPassword(email:String,password:String, callback: (FirebaseUser?) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("Auth","Sign Up Successful")
                callback(getCurrentUser())
            }
            else{
                Log.e("Auth","Something Went Wrong!! Please Try Again")
                Log.e("Auth",it.exception.toString())
                callback(null)
            }
        }
    }

    fun signInWithEmailAndPassword(email: String,password: String,callback: (FirebaseUser?) -> Unit){
        if(getCurrentUser() != null){
            Log.i("Auth","User already logged in")
            callback(getCurrentUser())
            return
        }

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("Auth","Authentication Successful ${getCurrentUser()}")
                callback(getCurrentUser())
            }
            else{
                Log.i("Auth","Authentication Failed ${it.exception}")
                callback(null)
            }
        }
    }

    fun signOut() = auth.signOut()

}