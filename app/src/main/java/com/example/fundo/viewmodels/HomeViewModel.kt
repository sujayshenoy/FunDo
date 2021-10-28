package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.services.Auth

class HomeViewModel:ViewModel() {
    private var _logoutStatus = MutableLiveData<Boolean>()
    var logoutStatus = _logoutStatus as LiveData<Boolean>

    private var _goToAuthenticationActivity = MutableLiveData<Boolean>()
    val goToAuthenticationActivity = _goToAuthenticationActivity as LiveData<Boolean>

    fun setGoToAuthenticationActivity(status:Boolean) {
        _goToAuthenticationActivity.value = status
    }

    fun logout() {
        Auth.signOut(){
            _logoutStatus.value = it
        }
    }
}