package com.example.fundo.ui.note

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.launch

class NoteViewModel: ViewModel() {
    private val _getLabels = MutableLiveData<ArrayList<Label>>()
    val getLabels = _getLabels as LiveData<ArrayList<Label>>

    private val _linkLabelStatus = MutableLiveData<Boolean>()
    val linkLabelStatus = _linkLabelStatus as LiveData<Boolean>

    fun getLabels(context: Context, firebaseID: String, user:User) {
        viewModelScope.launch {
            DatabaseService.getInstance(context).getLabelsWithNote(firebaseID, user).let {
                _getLabels.postValue(it)
            }
        }
    }

    fun linkNoteLabels(context: Context, firebaseID: String, labelList: ArrayList<Label>, user: User) {
        viewModelScope.launch {
            DatabaseService.getInstance(context).linkNoteLabels(firebaseID, labelList, user).let {
                _linkLabelStatus.postValue(it)
            }
        }
    }

    fun unlinkNoteLabels(context: Context, linkID: String, user: User) {
        viewModelScope.launch {
            DatabaseService.getInstance(context).removeNoteLabelLink(linkID, user)
        }
    }
}