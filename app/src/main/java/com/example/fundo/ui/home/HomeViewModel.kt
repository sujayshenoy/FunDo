package com.example.fundo.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.services.StorageService
import com.example.fundo.data.wrappers.Note
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

    fun addNoteToDB(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.addNoteToDB(note).also {
                _addNoteToDB.postValue(it)
            }
        }
    }

    fun updateNoteInDB(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.updateNoteInDB(note).also {
                _updateNoteInDB.postValue(it)
            }
        }
    }

    fun deleteNoteFromDB(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.deleteNoteFromDB(note).also {
                _deleteNoteFromDB.postValue(it)
            }
        }
    }

    fun getNotesFromDB(){
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getNotesFromDB().also {
                _getNotesFromDB.postValue(it)
            }
        }
    }

    fun logout() {
        FirebaseAuthService.signOut(){
            _logoutStatus.value = it
        }
    }
}