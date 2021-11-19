package com.example.fundo.ui.home.label

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.launch

class LabelViewModel : ViewModel() {
    private val _addLabelStatus = MutableLiveData<Label>()
    val addLabelStatus = _addLabelStatus as LiveData<Label>

    private val _deleteLabelStatus = MutableLiveData<Label>()
    val deleteLabelStatus = _deleteLabelStatus as LiveData<Label>

    private val _updateLabelStatus = MutableLiveData<Label>()
    val updateLabelStatus = _deleteLabelStatus as LiveData<Label>

    fun addLabel(context: Context, label: Label, user: User) {
        viewModelScope.launch {
            val newLabel = DatabaseService.getInstance(context).addLabelToDB(label, user)
            _addLabelStatus.postValue(newLabel)
        }
    }

    fun deleteLabel(context: Context, label: Label, user: User) {
        viewModelScope.launch {
            val deletedLabel = DatabaseService.getInstance(context).deleteLabelFromDB(label, user)
            _deleteLabelStatus.postValue(deletedLabel)
        }
    }

    fun updateLabel(context: Context, label: Label, user: User) {
        viewModelScope.launch {
            val updateLabel = DatabaseService.getInstance(context).updateLabel(label, user)
            _updateLabelStatus.postValue(updateLabel)
        }
    }
}