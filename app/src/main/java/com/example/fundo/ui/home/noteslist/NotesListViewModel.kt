package com.example.fundo.ui.home.noteslist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.common.Logger
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.services.SyncDB
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesListViewModel : ViewModel() {
    private val _addNoteToDB = MutableLiveData<Note>()
    val addNoteToDB = _addNoteToDB as LiveData<Note>

    private val _getNotesFromDB = MutableLiveData<List<Note>>()
    val getNotesFromDB = _getNotesFromDB as LiveData<List<Note>>

    private val _getArchivedNotesFromDB = MutableLiveData<List<Note>>()
    val getArchivedNotesFromDB = _getArchivedNotesFromDB as LiveData<List<Note>>

    private val _getReminderNotesFromDB = MutableLiveData<List<Note>>()
    val getReminderNotesFromDB = _getReminderNotesFromDB as LiveData<List<Note>>

    private val _updateNoteInDB = MutableLiveData<Note>()
    val updateNoteInDB = _updateNoteInDB as LiveData<Note>

    private val _deleteNoteFromDB = MutableLiveData<Note>()
    val deleteNoteFromDB = _deleteNoteFromDB as LiveData<Note>

    private val _syncDBStatus = MutableLiveData<Boolean>()
    val syncDBStatus = _syncDBStatus as LiveData<Boolean>

    fun addNoteToDB(context: Context, note: Note, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).addNoteToDB(note, user).also {
                _addNoteToDB.postValue(it)
            }
        }
    }

    fun updateNoteInDB(context: Context, note: Note, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).updateNoteInDB(note, user).also {
                _updateNoteInDB.postValue(it)
            }
        }
    }

    fun deleteNoteFromDB(context: Context, note: Note, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).deleteNoteFromDB(note, user).also {
                _deleteNoteFromDB.postValue(it)
            }
        }
    }

    fun getNotesFromDB(context: Context, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getNotesFromDB(user).let {
                _getNotesFromDB.postValue(it)
            }
        }
    }

    fun getArchivedNotes(context: Context, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getArchivedNotes(user).let {
                _getArchivedNotesFromDB.postValue(it)
            }
        }
    }

    fun getReminderNotes(context: Context, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getReminderNotes(user).let {
                _getReminderNotesFromDB.postValue(it)
            }
        }
    }

    fun syncDB(context: Context, user: User) {
        viewModelScope.launch {
            Logger.logInfo("Starting sync")
            SyncDB(context).syncNow(user)
            _syncDBStatus.postValue(true)
            Logger.logInfo("Sync Complete")
        }
    }
}