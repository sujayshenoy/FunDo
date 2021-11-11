package com.example.fundo.ui.authentication.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.auth.services.FirebaseAuthService

class ResetPasswordViewModel:ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<Boolean>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

    fun resetPassword(email:String) {
        FirebaseAuthService.resetPassword(email){
            _resetPasswordStatus.value = it
        }
    }
}