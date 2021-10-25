package com.example.fundo.services

import android.util.Log
import android.webkit.ValueCallback
import android.widget.Toast
import com.example.fundo.models.User
import com.example.fundo.utils.Utilities
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

object Auth {
    private val auth : FirebaseAuth = Firebase.auth

    fun getCurrentUser() = auth.currentUser //null if no user signed in

    fun signUpWithEmailAndPassword(user: User, password:String, callback: (FirebaseUser?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("Auth","Sign Up Successful")
                Database.addUserToDB(user)
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

    fun handleFacebookLogin(accessToken: AccessToken, callback: (FirebaseUser?) -> Unit) {
        Log.d("Facebook-OAuth",accessToken.toString())

        var credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("Auth","signInWithCredential;success")
                    var firebaseUser = getCurrentUser()
                    var email = firebaseUser?.email.toString()
                    var name = firebaseUser?.displayName.toString()
                    var phone = firebaseUser?.phoneNumber.toString()

                    var user = User(name,email,phone)
                    Database.addUserToDB(user)
                    callback(getCurrentUser())
                }
                else{
                    Log.d("Auth","signInWithCredential:failure",it.exception)
                    callback(null)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()
    }

    fun resetPassword(email:String,callback: (String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                callback("Password reset link has been sent to $email")
            }
            else{
                callback("Email not registered! Please register from registration screen")
            }
        }
    }
}