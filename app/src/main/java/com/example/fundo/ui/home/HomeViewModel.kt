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
import com.example.fundo.data.services.StorageService
import com.example.fundo.data.services.SyncDB
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel:ViewModel() {
    private var _logoutStatus = MutableLiveData<Boolean>()
    var logoutStatus = _logoutStatus as LiveData<Boolean>

    private var _goToAuthenticationActivity = MutableLiveData<Boolean>()
    val goToAuthenticationActivity = _goToAuthenticationActivity as LiveData<Boolean>

    private var _setUserAvatarStatus = MutableLiveData<Bitmap>()
    val setUserAvatarStatus = _setUserAvatarStatus as LiveData<Bitmap>

    private val _addNoteToDB = MutableLiveData<Note>()
    val addNoteToDB = _addNoteToDB as LiveData<Note>

    private val _getNotesFromDB = MutableLiveData<List<Note>>()
    val getNotesFromDB = _getNotesFromDB as LiveData<List<Note>>

    private val _updateNoteInDB = MutableLiveData<Note>()
    val updateNoteInDB = _updateNoteInDB as LiveData<Note>

    private val _deleteNoteFromDB = MutableLiveData<Note>()
    val deleteNoteFromDB = _deleteNoteFromDB as LiveData<Note>

    private val _getUserFromDB = MutableLiveData<User>()
    val getUserFromDB = _getUserFromDB as LiveData<User>

    private val _syncDBStatus = MutableLiveData<Boolean>()
    val syncDBStatus = _syncDBStatus as LiveData<Boolean>

    fun setGoToAuthenticationActivity(status:Boolean) {
        _goToAuthenticationActivity.value = status
    }

    fun setUserAvatar(bitmap: Bitmap){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                StorageService.addUserAvatar(bitmap).also{
                    _setUserAvatarStatus.postValue(bitmap)
                }
            } catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }

    fun getUserAvatar(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                StorageService.getUserAvatar().also {
                    _setUserAvatarStatus.postValue(it)
                }
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
    }

    fun addNoteToDB(context: Context,note: Note,user: User){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.addNoteToDB(context,note,user).also {
                _addNoteToDB.postValue(it)
            }
        }
    }

    fun updateNoteInDB(context: Context,note: Note,user: User){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.updateNoteInDB(context,note,user).also {
                _updateNoteInDB.postValue(it)
            }
        }
    }

    fun deleteNoteFromDB(context: Context,note: Note,user: User){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.deleteNoteFromDB(context,note,user).also {
                _deleteNoteFromDB.postValue(it)
            }
        }
    }

    fun getNotesFromDB(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getNotesFromDB(user).let {
                _getNotesFromDB.postValue(it)
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuthService.signOut(context){
                _logoutStatus.postValue(it)
            }
        }
    }

    fun getUserFromDB() {
        viewModelScope.launch{
            val userId = SharedPrefUtil.getUserId()
            if(userId > 0){
                val user = DatabaseService.getUserFromDB(userId)
                _getUserFromDB.postValue(user)
            }
        }
    }

    fun syncDB(context: Context,user: User) {
        viewModelScope.launch {
            Logger.logInfo("Starting sync")
            SyncDB.syncNow(context, user)
            _syncDBStatus.postValue(true)
            Logger.logInfo("Sync Complete")
        }
    }
}