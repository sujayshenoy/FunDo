package com.example.fundo.data.services

import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface

object DatabaseService : DatabaseInterface {
    override fun addUserToDB(user : User, callback : (Boolean) -> Unit){
        FirebaseDatabaseService.addUserToDB(user, callback)
    }

    override fun getUserFromDB(callback: (Boolean) -> Unit) {
        FirebaseDatabaseService.getUserFromDB(callback)
    }

    override fun addNoteToDB(note: Note, callback: (Note?) -> Unit) {
        FirebaseDatabaseService.addNoteToDB(note, callback)
    }

    override fun getNotesFromDB(callback: (List<Note>?) -> Unit) {
        FirebaseDatabaseService.getNotesFromDB(callback)
    }

    override fun updateNoteInDB(note: Note, callback: (Note?) -> Unit){
        FirebaseDatabaseService.updateNoteInDB(note, callback)
    }

    override fun deleteNoteFromDB(note: Note, callback: (Note?) -> Unit){
        FirebaseDatabaseService.deleteNoteFromDB(note, callback)
    }
}