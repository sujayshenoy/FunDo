package com.example.fundo.interfaces

import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User

interface DatabaseInterface {
    fun addUserToDB(user : User, callback : (Boolean) -> Unit)

    fun getUserFromDB(callback: (Boolean) -> Unit)

    fun addNoteToDB(note: Note, callback: (Note?) -> Unit)

    fun getNotesFromDB(callback: (List<Note>?) -> Unit)

    fun updateNoteInDB(note: Note, callback: (Note?) -> Unit)

    fun deleteNoteFromDB(note: Note, callback: (Note?) -> Unit)
}