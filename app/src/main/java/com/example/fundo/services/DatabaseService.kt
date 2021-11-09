package com.example.fundo.services

import com.example.fundo.wrapper.Note
import com.example.fundo.wrapper.User

object DatabaseService {
    fun addUserToDB(user : User, callback : (Boolean) -> Unit){
        FirebaseDatabaseService.addUserToDB(user,callback)
    }

    fun getUserFromDB(callback: (Boolean) -> Unit) {
        FirebaseDatabaseService.getUserFromDB(callback)
    }

    fun addNoteToDB(note: Note, callback: (Note?) -> Unit) {
        FirebaseDatabaseService.addNoteToDB(note,callback)
    }

    fun getNotesFromDB(callback: (List<Note>?) -> Unit) {
        FirebaseDatabaseService.getNotesFromDB(callback)
    }

    fun updateNoteInDB(note: Note, callback: (Note?) -> Unit){
        FirebaseDatabaseService.updateNoteInDB(note,callback)
    }

    fun deleteNoteFromDB(note: Note, callback: (Note?) -> Unit){
        FirebaseDatabaseService.deleteNoteFromDB(note,callback)
    }
}