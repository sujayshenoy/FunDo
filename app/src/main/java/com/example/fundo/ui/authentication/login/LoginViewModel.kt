package com.example.fundo.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.data.wrappers.User
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.data.services.DatabaseService
import com.facebook.AccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel:ViewModel() {
    private val _emailPassLoginStatus = MutableLiveData<User>()
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>

    private val _facebookLoginStatus = MutableLiveData<User>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>

    fun loginWithEmailAndPassword(email:String,password:String) {
        FirebaseAuthService.signInWithEmailAndPassword(email,password){ user ->
            viewModelScope.launch(Dispatchers.IO){
                if(user?.loginStatus == true) {
                    DatabaseService.getUserFromDB()
                }
                _emailPassLoginStatus.postValue(user)
            }
        }
    }

    fun loginWithFacebook(accessToken: AccessToken) {
        FirebaseAuthService.handleFacebookLogin(accessToken){ user ->
            viewModelScope.launch(Dispatchers.IO) {
                if (user != null) {
                    DatabaseService.addUserToDB(user)
                }
                _facebookLoginStatus.postValue(user)
            }
        }
    }
}