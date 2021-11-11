package com.example.fundo.interfaces

import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User

interface DatabaseInterface {
    suspend fun addUserToDB(user : User):Boolean

    suspend fun getUserFromDB():Boolean

    suspend fun addNoteToDB(note: Note) : Note?

    suspend fun getNotesFromDB() : List<Note>?

    suspend fun updateNoteInDB(note: Note) : Note?

    suspend fun deleteNoteFromDB(note: Note):Note?
}