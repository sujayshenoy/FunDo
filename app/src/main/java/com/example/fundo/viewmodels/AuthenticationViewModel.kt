package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.R
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.example.fundo.ui.authentication.LoginFragment
import com.example.fundo.utils.Utilities
import com.facebook.AccessToken

class AuthenticationViewModel:ViewModel() {
    private val _goToLoginScreen = MutableLiveData<Boolean>()
    private val _goToSignUpScreen = MutableLiveData<Boolean>()
    private val _goToResetPassword = MutableLiveData<Boolean>()
    private val _goToHome = MutableLiveData<Boolean>()
    private val _emailPassLoginStatus = MutableLiveData<User>()
    private val _facebookLoginStatus = MutableLiveData<User>()
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    private val _resetPasswordStatus = MutableLiveData<Boolean>()

    val goToLoginScreen = _goToLoginScreen as LiveData<Boolean>
    val goToSignUpScreen = _goToSignUpScreen as LiveData<Boolean>
    val goToResetPassword = _goToResetPassword as LiveData<Boolean>
    val goToHome = _goToHome as LiveData<Boolean>
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<User>
    val facebookLoginStatus = _facebookLoginStatus as LiveData<User>
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

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

    fun signUpWithEmailAndPassword(email: String,password: String) {
        Auth.signUpWithEmailAndPassword(email,password){
            _emailPassSignUpStatus.value = it
        }
    }

    fun resetPassword(email:String) {
        Auth.resetPassword(email){
            _resetPasswordStatus.value = it
        }
    }
}