package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.common.NetworkService
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class DatabaseService(private val context: Context) : DatabaseInterface {    //to singleton
    private val sqLiteDatabaseService: SqLiteDatabaseService = SqLiteDatabaseService(context)
    private val firebaseDatabaseService = FirebaseDatabaseService.getInstance()


    companion object {
        private val instance: DatabaseService? by lazy { null }

        fun getInstance(context: Context): DatabaseService = instance ?: DatabaseService(context)
    }

    suspend fun addCloudDataToLocalDB(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val noteListFromCloud = firebaseDatabaseService.getNotesFromDB(user)
            if (noteListFromCloud != null) {
                for (i in noteListFromCloud) {
                    sqLiteDatabaseService.addNoteToDB(
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
                val newUser = firebaseDatabaseService.getUserFromDB(user.firebaseId)!!
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
                return@withContext firebaseDatabaseService.addUserToDB(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun checkUserInCloudDB(userId: String): Boolean {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.getUserFromDB(userId) != null
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

    suspend fun addNoteToLocalDB(note: Note, user: User?): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.addNoteToDB(note, user, note.lastModified)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun addNoteToDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val newNote = firebaseDatabaseService.addNoteToDB(
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.addNoteToDB(
                        newNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.addNoteToDB(
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

    suspend fun getNotesFromCloud(user: User?): List<Note>? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.getNotesFromDB(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun updateNoteInDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val updatedNote = firebaseDatabaseService.updateNoteInDB(
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.updateNoteInDB(
                        updatedNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.updateNoteInDB(
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
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return try {
            return withContext(Dispatchers.IO) {
                val now = Date(System.currentTimeMillis())
                if (NetworkService.isNetworkConnected(context)) {
                    val deletedNote = firebaseDatabaseService.deleteNoteFromDB(
                        note,
                        user,
                        timeStamp = now
                    )!!
                    return@withContext sqLiteDatabaseService.deleteNoteFromDB(
                        deletedNote,
                        timeStamp = now
                    )
                } else {
                    return@withContext sqLiteDatabaseService.deleteNoteFromDB(
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

    suspend fun getOpCode(note: Note): Int {
        return withContext(Dispatchers.IO) {
            return@withContext sqLiteDatabaseService.getOpCode(note)
        }
    }

    suspend fun clearNoteAndOp() {
        sqLiteDatabaseService.clearNoteAndOp()
    }

    fun clearLocalDB() {
        sqLiteDatabaseService.clearAll()
    }
}