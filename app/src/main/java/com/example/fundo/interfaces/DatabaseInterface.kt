package com.example.fundo.interfaces

import android.content.Context
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import java.util.*

interface DatabaseInterface {
    suspend fun addUserToDB(user: User): User?

    suspend fun getUserFromDB(userID: Long): User?

    suspend fun addNoteToDB(
        context: Context,
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?

    suspend fun getNotesFromDB(user: User?): List<Note>?

    suspend fun updateNoteInDB(
        context: Context,
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?

    suspend fun deleteNoteFromDB(
        context: Context,
        note: Note,
        user: User? = null,
        timeStamp: Date? = null,
        onlineMode: Boolean = true
    ): Note?
}