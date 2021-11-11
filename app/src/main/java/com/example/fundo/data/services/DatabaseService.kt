package com.example.fundo.data.services

import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseService : DatabaseInterface {
    override suspend fun addUserToDB(user : User) : Boolean{
        return try{
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.addUserToDB(user)
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            false
        }
    }

    override suspend fun getUserFromDB() : Boolean {
        return try {
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.getUserFromDB()
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            false
        }
    }

    override suspend fun addNoteToDB(note: Note):Note? {
        return try {
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.addNoteToDB(note)
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            null
        }
    }

    override suspend fun getNotesFromDB():List<Note>? {
        return try {
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.getNotesFromDB()
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            null
        }
    }

    override suspend fun updateNoteInDB(note: Note):Note?{
        return try {
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.updateNoteInDB(note)
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            null
        }
    }

    override suspend fun deleteNoteFromDB(note: Note):Note?{
        return try {
            return withContext(Dispatchers.IO){
                return@withContext FirebaseDatabaseService.deleteNoteFromDB(note)
            }
        } catch (ex:FirebaseException){
            ex.printStackTrace()
            null
        }
    }
}