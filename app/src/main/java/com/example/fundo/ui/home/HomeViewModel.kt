package com.example.fundo.ui.home

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.Logger
import com.example.fundo.common.SharedPrefUtil
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.services.CloudStorageService
import com.example.fundo.data.services.SyncDB
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel : ViewModel() {
    private var _logoutStatus = MutableLiveData<Boolean>()
    var logoutStatus = _logoutStatus as LiveData<Boolean>

    private var _goToAuthenticationActivity = MutableLiveData<Boolean>()
    val goToAuthenticationActivity = _goToAuthenticationActivity as LiveData<Boolean>

    private var _setUserAvatarStatus = MutableLiveData<Bitmap>()
    val setUserAvatarStatus = _setUserAvatarStatus as LiveData<Bitmap>

    private val _getUserFromDB = MutableLiveData<User>()
    val getUserFromDB = _getUserFromDB as LiveData<User>

    private val _goToNotesList = MutableLiveData<Boolean>()
    val goToNotesList = _goToNotesList as LiveData<Boolean>

    fun goToAuthenticationActivity(status: Boolean) {
        _goToAuthenticationActivity.value = status
    }

    fun goToNotesListFragment() {
        _goToNotesList.value = true
    }

    fun setUserAvatar(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                CloudStorageService.setUserAvatar(bitmap).also {
                    _setUserAvatarStatus.postValue(bitmap)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun getUserAvatar() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                CloudStorageService.getUserAvatar().also {
                    _setUserAvatarStatus.postValue(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuthService.signOut(context) {
                _logoutStatus.postValue(it)
            }
        }
    }

    fun getUserFromDB(context: Context) {
        viewModelScope.launch {
            val userId = SharedPrefUtil.getUserId()
            if (userId > 0) {
                val user = DatabaseService.getInstance(context).getUserFromDB(userId)
                _getUserFromDB.postValue(user)
            }
        }
    }
}