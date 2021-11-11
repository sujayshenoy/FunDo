package com.example.fundo.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthenticationSharedViewModel():ViewModel() {
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