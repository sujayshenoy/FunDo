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

    private val _getNotesPaged = MutableLiveData<List<Note>>()
    val getNotesPaged = _getNotesPaged as LiveData<List<Note>>

    private val _getArchivesPaged = MutableLiveData<List<Note>>()
    val getArchivesPaged = _getArchivesPaged as LiveData<List<Note>>

    private val _getRemindersPaged = MutableLiveData<List<Note>>()
    val getRemindersPaged = _getRemindersPaged as LiveData<List<Note>>

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

    private val _getNotesCount = MutableLiveData<Int>()
    val getNotesCount = _getNotesCount as LiveData<Int>

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
            DatabaseService.getInstance(context).getNotesPaged(10, 0).let {
                _getNotesFromDB.postValue(it)
            }
        }
    }

    fun getNotesPaged(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getNotesPaged(limit, offset).let {
                _getNotesPaged.postValue(it)
            }
        }
    }

    fun getArchivesPaged(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getArchivesPaged(limit, offset).let {
                _getArchivesPaged.postValue(it)
            }
        }
    }

    fun getRemindersPaged(context: Context, limit: Int, offset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getRemindersPaged(limit, offset).let {
                _getRemindersPaged.postValue(it)
            }
        }
    }

    fun getNotesCount(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getNotesCount().let {
                _getNotesCount.postValue(it)
            }
        }
    }

    fun getArchivedNotes(context: Context, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getArchivesPaged(10, 0).let {
                _getArchivedNotesFromDB.postValue(it)
            }
        }
    }

    fun getReminderNotes(context: Context, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseService.getInstance(context).getRemindersPaged(10, 0).let {
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