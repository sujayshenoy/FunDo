package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.facebook.AccessToken

class AuthenticationViewModel:ViewModel() {
    private val _goToLoginScreen = MutableLiveData<Boolean>()
    private val _goToSignUpScreen = MutableLiveData<Boolean>()
    private val _goToResetPassword = MutableLiveData<Boolean>()
    private val _goToHome = MutableLiveData<Boolean>()
    private val _emailPassLoginStatus = MutableLiveData<User>()
    private val _facebookLoginStatus = MutableLiveData<User>()

    val goToLoginScreen = _goToLoginScreen as LiveData<Boolean>
    val goToSignUpScreen = _goToSignUpScreen as LiveData<Boolean>
    val goToResetPassword = _goToResetPassword as LiveData<Boolean>
    val goToHome = _goToHome as LiveData<Boolean>
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>

    fun setGoToLoginScreenStatus(flag:Boolean){
        _goToLoginScreen.value = flag
    }

    fun setGoToSignUpScreenStatus(flag: Boolean){
        _goToSignUpScreen.value = flag
    }

    fun setGoToResetPassword(flag: Boolean){
        _goToResetPassword.value = flag
    }

    fun setGoToHome(flag: Boolean){
        _goToHome.value = flag
    }

    fun loginWithEmailAndPassword(email:String,password:String) {
        Auth.signInWithEmailAndPassword(email,password){
            _emailPassLoginStatus.value = it
        }
    }

    fun loginWithFacebook(accessToken: AccessToken) {
        Auth.handleFacebookLogin(accessToken){
            _facebookLoginStatus.value = it
        }
    }

}