package com.example.fundo.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.services.AuthService
import com.example.fundo.services.DatabaseService
import com.example.fundo.services.FirebaseDatabaseService
import com.example.fundo.services.StorageService
import com.example.fundo.wrapper.Note

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

    fun addNoteToDB(note: Note){
        DatabaseService.addNoteToDB(note){
            _addNoteToDB.value = it
        }
    }

    fun updateNoteInDB(note: Note){
        DatabaseService.updateNoteInDB(note){
            _updateNoteInDB.value = it
        }
    }

    fun deleteNoteFromDB(note: Note){
        DatabaseService.deleteNoteFromDB(note){
            _deleteNoteFromDB.value = note
        }
    }

    fun getNotesFromDB(){
        DatabaseService.getNotesFromDB {
            _getNotesFromDB.value = it
        }
    }

    fun logout() {
        AuthService.signOut(){
            _logoutStatus.value = it
        }
    }
}