package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.common.Logger
import com.example.fundo.common.NetworkService
import com.example.fundo.data.wrappers.Label
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

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
            listOf()
        }
    }

    suspend fun getNotesCount(): Int {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getNotesCount()
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            0
        }
    }

    suspend fun getNotesPaged(limit: Int, offset: Int): List<Note> {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getNotesPaged(limit, offset)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            listOf()
        }
    }

    suspend fun getArchivesPaged(limit: Int, offset: Int): List<Note> {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getArchivedNotesPaged(limit, offset)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            listOf()
        }
    }

    suspend fun getRemindersPaged(limit: Int, offset: Int): List<Note> {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getReminderNotesPaged(limit, offset)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            listOf()
        }
    }

    suspend fun getArchivedNotes(user: User?): List<Note> {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getArchivedNotes(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            listOf()
        }
    }

    suspend fun getReminderNotes(user: User?): List<Note> {
        Logger.logDbInfo("DATA layer: called reminder")
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext sqLiteDatabaseService.getReminderNotes(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            listOf()
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

    suspend fun addLabelToDB(label: Label, user: User?, timeStamp: Date? = null): Label? {
        val now = Date(System.currentTimeMillis())
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.addLabelToDB(label, user, now)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun deleteLabelFromDB(label: Label, user: User?): Label? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.deleteLabelFromDB(label, user)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun updateLabel(label: Label, user: User?): Label? {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.updateLabel(
                    label, user,
                    Date(System.currentTimeMillis())
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun getLabels(user: User?): ArrayList<Label> {
        return try {
            return withContext(Dispatchers.IO) {
                return@withContext firebaseDatabaseService.getLabels(user)
            }
        } catch (ex: FirebaseException) {
            ex.printStackTrace()
            ArrayList()
        }
    }

    suspend fun linkNoteLabels(noteID: String, labels: ArrayList<Label>, user: User?): Boolean {
        return try {
            return withContext(Dispatchers.IO) {
                labels.forEach { label ->
                    firebaseDatabaseService.linkNoteLabel(noteID,label.firebaseId,user)
                }
                true
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun removeNoteLabelLink(linkId: String, user: User?): Boolean {
        return try {
            return withContext(Dispatchers.IO) {
                firebaseDatabaseService.removeNoteLabelLink(linkId, user)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun getLabelsWithNote(noteID: String, user: User?): ArrayList<Label> {
        return try {
            return withContext(Dispatchers.IO) {
                firebaseDatabaseService.getLabelsWithNote(noteID, user)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            ArrayList()
        }
    }

    suspend fun getNotesWithLabel(labelID: String, user: User?): ArrayList<Note> {
        return try {
            return withContext(Dispatchers.IO) {
                firebaseDatabaseService.getNotesWithLabel(labelID, user)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            ArrayList()
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