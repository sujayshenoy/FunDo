package com.example.fundo.interfaces

import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import java.util.*

interface DatabaseInterface {
    suspend fun addUserToDB(user: User): User?

    suspend fun getUserFromDB(userID: Long): User?

    suspend fun addNoteToDB(
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?

    suspend fun getNotesFromDB(user: User?): List<Note>?

    suspend fun updateNoteInDB(
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?

    suspend fun deleteNoteFromDB(
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?
}