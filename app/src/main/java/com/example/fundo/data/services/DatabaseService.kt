package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.common.Logger
import com.example.fundo.common.NetworkService
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

    suspend fun addCloudDataToLocalDB(context: Context, user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val noteListFromCloud = FirebaseDatabaseService.getNotesFromDB(user)
            if (noteListFromCloud != null) {
                for (i in noteListFromCloud) {
                    sqLiteDatabaseService.addNoteToDB(
                        context,
                        i,
                        timeStamp = Date(System.currentTimeMillis())
                    )
                }
            }
            true
        }
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

    suspend fun checkUserInCloudDB(userId: String): Boolean {
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

    suspend fun addNoteToLocalDB(context: Context,note: Note,user: User?) : Note?{
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.addNoteToDB(context,note,user,note.lastModified)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun addNoteToDB(
        context: Context,
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val newNote = FirebaseDatabaseService.addNoteToDB(
                        context,
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.addNoteToDB(
                        context,
                        newNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.addNoteToDB(
                        context,
                        note,
                        timeStamp = now,
                        onlineMode = false
                    )
                }
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

    suspend fun getNotesFromCloud(user: User?):List<Note>? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext FirebaseDatabaseService.getNotesFromDB(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun updateNoteInDB(
        context: Context,
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val updatedNote = FirebaseDatabaseService.updateNoteInDB(
                        context,
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.updateNoteInDB(
                        context,
                        updatedNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.updateNoteInDB(
                        context,
                        note,
                        timeStamp = now,
                        onlineMode = false
                    )
                }
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun deleteNoteFromDB(
        context: Context,
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val deletedNote = FirebaseDatabaseService.deleteNoteFromDB(
                        context,
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.deleteNoteFromDB(
                        context,
                        deletedNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.deleteNoteFromDB(
                        context,
                        note,
                        timeStamp = now,
                        onlineMode = false
                    )
                }
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun getOpCode(note: Note):Int{
        return withContext(Dispatchers.IO){
            val opCode = sqLiteDatabaseService.getOpCode(note)
            return@withContext opCode
        }
    }

    suspend fun clearNoteAndOp(){
        sqLiteDatabaseService.clearNoteAndOp()
    }

    fun clearLocalDB(){
        sqLiteDatabaseService.clearAll()
    }
}