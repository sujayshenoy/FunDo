package com.example.fundo.auth.services

import com.example.fundo.common.Logger
import com.example.fundo.data.wrappers.User
import com.example.fundo.common.SharedPrefUtil
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuthService {
    private val auth : FirebaseAuth = Firebase.auth

    fun getCurrentUser() = auth.currentUser

    fun signUpWithEmailAndPassword(email: String, password:String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Logger.logAuthInfo("Sign Up Successful")
                callback(true)
            }
            else{
                Logger.logAuthError("Something Went Wrong!! Please Try Again")
                Logger.logAuthError(it.exception.toString())
                callback(false)
            }
        }
    }

    fun signInWithEmailAndPassword(email: String,password: String,callback: (User?) -> Unit){
        if(getCurrentUser() != null){
            Logger.logAuthInfo("User already logged in")
            val curUser = getCurrentUser()
            val user = User(curUser?.displayName.toString(),curUser?.email.toString(),
                curUser?.phoneNumber.toString(),true)
            callback(user)
            return
        }

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            var user: User?
            if(it.isSuccessful){
                Logger.logAuthInfo("Authentication Successful ${getCurrentUser()}")
                val curUser = getCurrentUser()
                user = User(curUser?.displayName.toString(),curUser?.email.toString(),
                    curUser?.phoneNumber.toString(),true)
                callback(user)
            }
            else{
                Logger.logAuthError("Authentication Failed ${it.exception}")
                user = User("","","",false)
                callback(user)
            }
        }
    }

    fun handleFacebookLogin(accessToken: AccessToken, callback: (User?) -> Unit) {
        var credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                var user: User?
                if(it.isSuccessful){
                    Logger.logAuthInfo("facebook;signInWithCredential;success")
                    var curUser = getCurrentUser()
                    var email = curUser?.email.toString()
                    var name = curUser?.displayName.toString()
                    var phone = curUser?.phoneNumber.toString()

                    var user = User(name,email,phone)
                    callback(user)
                }
                else{
                    Logger.logAuthError("Authentication Failed ${it.exception}")
                    user = User("","","",false)
                    callback(user)
                }
            }
    }

    fun signOut(callback: (Boolean) -> Unit) {
        SharedPrefUtil.clearAll()
        auth.signOut()
        LoginManager.getInstance().logOut()
        callback(true)
    }

    fun resetPassword(email:String,callback: (Boolean) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true)
            }
            else{
                callback(false)
            }
        }
    }
}