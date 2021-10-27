package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.facebook.AccessToken

class LoginViewModel:ViewModel() {
    private val _emailPassLoginStatus = MutableLiveData<User>()
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>

    private val _facebookLoginStatus = MutableLiveData<User>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>

    fun loginWithEmailAndPassword(email:String,password:String) {
        Auth.signInWithEmailAndPassword(email,password){ user ->
            Database.getUserFromDB {
                _emailPassLoginStatus.value = user
            }
        }
    }

    fun loginWithFacebook(accessToken: AccessToken) {
        Auth.handleFacebookLogin(accessToken){ user ->
            if (user != null) {
                Database.addUserToDB(user){
                    _facebookLoginStatus.value = user
                }
            }
        }
    }
}