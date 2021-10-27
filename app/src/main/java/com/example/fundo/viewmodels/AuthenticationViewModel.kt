package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.R
import com.example.fundo.models.DBUser
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.example.fundo.ui.authentication.LoginFragment
import com.example.fundo.utils.Utilities
import com.facebook.AccessToken

class AuthenticationViewModel():ViewModel() {
    private val _goToLoginScreen = MutableLiveData<Boolean>()
    private val _goToSignUpScreen = MutableLiveData<Boolean>()
    private val _goToResetPassword = MutableLiveData<Boolean>()
    private val _goToHome = MutableLiveData<Boolean>()

    val goToLoginScreen = _goToLoginScreen as LiveData<Boolean>
    val goToSignUpScreen = _goToSignUpScreen as LiveData<Boolean>
    val goToResetPassword = _goToResetPassword as LiveData<Boolean>
    val goToHome = _goToHome as LiveData<Boolean>

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
}