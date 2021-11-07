package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.wrapper.User
import com.example.fundo.services.AuthService
import com.example.fundo.services.DatabaseService
import com.facebook.AccessToken

class LoginViewModel:ViewModel() {
    private val _emailPassLoginStatus = MutableLiveData<User>()
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>

    private val _facebookLoginStatus = MutableLiveData<User>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>

    fun loginWithEmailAndPassword(email:String,password:String) {
        AuthService.signInWithEmailAndPassword(email,password){ user ->
            if(user?.loginStatus == true){
                DatabaseService.getUserFromDB {
                    _emailPassLoginStatus.value = user
                }
            }
            else{
                _emailPassLoginStatus.value = user
            }
        }
    }

    fun loginWithFacebook(accessToken: AccessToken) {
        AuthService.handleFacebookLogin(accessToken){ user ->
            if (user != null) {
                DatabaseService.addUserToDB(user){
                    _facebookLoginStatus.value = user
                }
            }
        }
    }
}