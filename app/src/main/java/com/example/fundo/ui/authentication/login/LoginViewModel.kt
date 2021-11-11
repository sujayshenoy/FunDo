package com.example.fundo.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.data.wrappers.User
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.data.services.DatabaseService
import com.facebook.AccessToken

class LoginViewModel:ViewModel() {
    private val _emailPassLoginStatus = MutableLiveData<User>()
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>

    private val _facebookLoginStatus = MutableLiveData<User>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>

    fun loginWithEmailAndPassword(email:String,password:String) {
        FirebaseAuthService.signInWithEmailAndPassword(email,password){ user ->
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
        FirebaseAuthService.handleFacebookLogin(accessToken){ user ->
            if (user != null) {
                DatabaseService.addUserToDB(user){
                    _facebookLoginStatus.value = user
                }
            }
        }
    }
}