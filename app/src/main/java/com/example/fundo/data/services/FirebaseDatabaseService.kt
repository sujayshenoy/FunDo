package com.example.fundo.data.services

import com.example.fundo.data.models.CloudDBNote
import com.example.fundo.data.models.CloudDBUser
import com.example.fundo.data.wrappers.User
import com.example.fundo.common.Utilities
import com.example.fundo.data.wrappers.Note
import com.example.fundo.common.Logger
import com.example.fundo.data.models.CloudDBLabel
import com.example.fundo.data.room.DateTypeConverters
import com.example.fundo.data.wrappers.Label
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseService : DatabaseInterface {
    private val fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseDatabaseService? = null
        fun getInstance(): FirebaseDatabaseService = instance ?: FirebaseDatabaseService()
    }

    override suspend fun addUserToDB(user: User): User? {
        val userDB = CloudDBUser(user.name, user.email, user.phone)
        return suspendCoroutine {
            fireStore.collection("users").document(user.firebaseId)
                .set(userDB).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(user))
                    } else {
                        Logger.logDbError("RealTimeDB: Add user to db failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun getUserFromDB(userID: Long): User? {
        return null
    }

    suspend fun getUserFromDB(userID: String): User? {
        return suspendCoroutine {
            fireStore.collection("users").document(userID)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.also { snapshot ->
                            Logger.logDbInfo("User from DB $snapshot")
                            if (snapshot != null) {
                                val userDb =
                                    Utilities.createUserFromHashMap(snapshot.data as HashMap<*, *>)
                                val user =
                                    User(
                                        userDb.name,
                                        userDb.email,
                                        userDb.phone,
                                        firebaseId = userID
                                    )
                                it.resumeWith(Result.success(user))
                            } else {
                                it.resumeWith(Result.success(null))
                            }
                        }
                    } else {
                        Logger.logDbError("RealtimeDB:Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }


    override suspend fun addNoteToDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        val dbNote = CloudDBNote(
            note.title,
            note.content,
            DateTypeConverters().fromDateTime(timeStamp).toString()
        )
        return suspendCoroutine {
            val id = fireStore.collection("users").document(user?.firebaseId!!)
                .collection("notes").document().id
            fireStore.collection("users").document(user.firebaseId)
                .collection("notes").document(id).set(dbNote)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        note.firebaseId = id
                        it.resumeWith(Result.success(note))
                    } else {
                        Logger.logDbError("RealTimeDB: Write Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun getNotesFromDB(user: User?): List<Note>? {
        val notes = mutableListOf<Note>()
        return suspendCoroutine {
            fireStore.collection("users").document(user?.firebaseId!!)
                .collection("notes").get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (i in task.result?.documents!!) {
                            val noteHashMap = i.data as HashMap<*, *>
                            val note = Note(
                                noteHashMap["title"].toString(),
                                noteHashMap["content"].toString(),
                                firebaseId = i.id,
                                lastModified = DateTypeConverters().toDateTime(noteHashMap["lastModified"].toString()) as Date
                            )
                            notes.add(note)
                        }
                        it.resumeWith(Result.success(notes))
                    } else {
                        Logger.logDbError("RealTimeDB: Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun updateNoteInDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        val noteMap = mapOf(
            "title" to note.title,
            "content" to note.content,
            "lastModified" to DateTypeConverters().fromDateTime(timeStamp).toString()
        )

        return suspendCoroutine {
            fireStore.collection("users").document(user?.firebaseId!!)
                .collection("notes").document(note.firebaseId).update(noteMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(note))
                    } else {
                        Logger.logDbError("RealTimeDB : Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun deleteNoteFromDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return suspendCoroutine {
            fireStore.collection("users").document(user?.firebaseId!!)
                .collection("notes").document(note.firebaseId).delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(note))
                    } else {
                        Logger.logDbError("RealTimeDB : Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun addLabelToDB(label: Label, user: User?, timeStamp: Date?): Label? {
        val dbLabel =
            CloudDBLabel(label.name, DateTypeConverters().fromDateTime(timeStamp).toString())
        return suspendCoroutine {
            val id = fireStore.collection("users").document(user?.firebaseId!!)
                .collection("labels").document().id
            fireStore.collection("users").document(user.firebaseId)
                .collection("labels").document(id).set(dbLabel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        label.firebaseId = id
                        it.resumeWith(Result.success(label))
                    } else {
                        Logger.logDbError("RealTimeDB: Write Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun deleteLabelFromDB(label: Label, user: User?): Label? {
        return suspendCoroutine {
            fireStore.collection("users").document(user?.firebaseId!!)
                .collection("labels").document(label.firebaseId).delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(label))
                    } else {
                        Logger.logDbError("RealTimeDB: Write Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun getLabels(user: User?): ArrayList<Label> {
        val labels = ArrayList<Label>()
        return suspendCoroutine {
            if (user != null) {
                fireStore.collection("users").document(user.firebaseId)
                    .collection("labels").get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (i in task.result?.documents!!) {
                                val labelHashMap = i.data as HashMap<*, *>
                                val label = Label(
                                    name = labelHashMap["name"].toString(),
                                    lastModified = DateTypeConverters().toDateTime(labelHashMap["lastModified"].toString()) as Date,
                                    firebaseId = i.id
                                )
                                labels.add(label)
                            }
                            it.resumeWith(Result.success(labels))
                        } else {
                            Logger.logDbError("RealTimeDB: Read Failed")
                            it.resumeWith(Result.failure(task.exception!!))
                        }
                    }
            }
        }
    }

    suspend fun updateLabel(label: Label, user: User?, timeStamp: Date?): Label? {
        val dbLabel = mapOf(
            "name" to label.name,
            "lastModified" to DateTypeConverters().fromDateTime(timeStamp).toString()
        )
        return suspendCoroutine {
            user?.firebaseId?.let { id ->
                fireStore.collection("users").document(id)
                    .collection("labels").document(label.firebaseId)
                    .update(dbLabel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            it.resumeWith(Result.success(label))
                        } else {
                            Logger.logDbError("RealTimeDB : Read Failed")
                            it.resumeWith(Result.failure(task.exception!!))
                        }
                    }
            }
        }
    }
}