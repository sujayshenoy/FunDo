package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.services.AuthService

class ResetPasswordViewModel:ViewModel() {
    private val _resetPasswordStatus = MutableLiveData<Boolean>()
    val resetPasswordStatus = _resetPasswordStatus as LiveData<Boolean>

    fun resetPassword(email:String) {
        AuthService.resetPassword(email){
            _resetPasswordStatus.value = it
        }
    }
}