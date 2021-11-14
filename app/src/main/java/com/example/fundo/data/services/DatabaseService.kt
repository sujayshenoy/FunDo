package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object DatabaseService : DatabaseInterface {
    private lateinit var sqLiteDatabaseService: SqLiteDatabaseService

    fun initSQLiteDatabase(context: Context) {
        sqLiteDatabaseService = SqLiteDatabaseService(context)
    }

    override suspend fun addUserToDB(user: User): User? {
        return try {
            return withContext(Dispatchers.IO) {
                val newUser = FirebaseDatabaseService.getUserFromDB(user.firebaseId)!!
                return@withContext sqLiteDatabaseService.addUserToDB(newUser)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

   suspend fun addUserToCloudDB(user: User): User? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext FirebaseDatabaseService.addUserToDB(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun checkUserInCloudDB(userId:String) : Boolean{
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext FirebaseDatabaseService.checkUserInDB(userId)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            false
        }
    }

    override suspend fun getUserFromDB(userID: Long): User? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getUserFromDB(userID)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun addCloudDataToLocalDB(user: User) : Boolean {
        return withContext(Dispatchers.IO){
            val noteListFromCloud = FirebaseDatabaseService.getNotesFromDB(user)
            if (noteListFromCloud != null) {
                for( i in noteListFromCloud){
                    sqLiteDatabaseService.addNoteToDB(i,timeStamp = Date(System.currentTimeMillis()))
                }
            }
            true
        }
    }

    override suspend fun addNoteToDB(note: Note, user: User?, timeStamp: Date?): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                val newNote = FirebaseDatabaseService.addNoteToDB(note,user,timeStamp=now)!!
                return@withContext sqLiteDatabaseService.addNoteToDB(newNote,timeStamp = now)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun getNotesFromDB(user: User?): List<Note>? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getNotesFromDB(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun updateNoteInDB(note: Note, user: User?, timeStamp: Date?): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                val updatedNote = FirebaseDatabaseService.updateNoteInDB(note,user,timeStamp=now)!!
                return@withContext sqLiteDatabaseService.updateNoteInDB(updatedNote,timeStamp = now)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun deleteNoteFromDB(note: Note, user: User?, timeStamp: Date?): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                val deletedNote = FirebaseDatabaseService.deleteNoteFromDB(note,user,timeStamp = now)!!
                return@withContext sqLiteDatabaseService.deleteNoteFromDB(deletedNote,timeStamp = now)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }
}