package com.example.fundo.ui.home

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.SharedPrefUtil
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.services.CloudStorageService
import com.example.fundo.data.wrappers.Label
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

    private val _goToCreateNewLabel = MutableLiveData<Boolean>()
    val goToCreateNewLabel = _goToCreateNewLabel as LiveData<Boolean>

    private val _getLabelFromDB = MutableLiveData<ArrayList<Label>>()
    val getLabelFromDB = _getLabelFromDB as LiveData<ArrayList<Label>>

    private val _goToArchive = MutableLiveData<Boolean>()
    val goToArchive = _goToArchive as LiveData<Boolean>

    private val _goToReminder = MutableLiveData<Boolean>()
    val goToReminder = _goToReminder as LiveData<Boolean>

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
            val userId = SharedPrefUtil.getInstance(context).getUserId()
            if (userId > 0) {
                val user = DatabaseService.getInstance(context).getUserFromDB(userId)
                _getUserFromDB.postValue(user)
            }
        }
    }

    fun getLabelFromDB(context: Context, user: User) {
        viewModelScope.launch {
            val label = DatabaseService.getInstance(context).getLabels(user)
            _getLabelFromDB.postValue(label)
        }
    }

    fun goToCreateNewLabel() {
        _goToCreateNewLabel.value = true
    }

    fun goToArchive() {
        _goToArchive.value = true
    }

    fun goToReminder() {
        _goToReminder.value = true
    }
}