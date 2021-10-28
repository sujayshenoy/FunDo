package com.example.fundo.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.services.AuthService
import com.example.fundo.services.StorageService

class HomeViewModel:ViewModel() {
    private var _logoutStatus = MutableLiveData<Boolean>()
    var logoutStatus = _logoutStatus as LiveData<Boolean>

    private var _goToAuthenticationActivity = MutableLiveData<Boolean>()
    val goToAuthenticationActivity = _goToAuthenticationActivity as LiveData<Boolean>

    private var _setUserAvatarStatus = MutableLiveData<Bitmap>()
    val setUserAvatarStatus = _setUserAvatarStatus as LiveData<Bitmap>

    fun setGoToAuthenticationActivity(status:Boolean) {
        _goToAuthenticationActivity.value = status
    }

    fun setUserAvatar(bitmap: Bitmap){
        StorageService.addUserAvatar(bitmap){
            if(it){
                _setUserAvatarStatus.value = bitmap
            }
        }
    }

    fun getUserAvatar(){
        StorageService.getUserAvatar {
            if(it!=null){
                _setUserAvatarStatus.value = it
            }
        }
    }

    fun logout() {
        AuthService.signOut(){
            _logoutStatus.value = it
        }
    }
}