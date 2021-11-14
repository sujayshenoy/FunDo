package com.example.fundo.data.services

import com.example.fundo.data.models.DBNote
import com.example.fundo.data.models.DBUser
import com.example.fundo.data.wrappers.User
import com.example.fundo.common.Utilities
import com.example.fundo.data.wrappers.Note
import com.example.fundo.common.Logger
import com.example.fundo.data.room.DateTypeConverters
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService : DatabaseInterface {
    private val database = Firebase.database.reference
    override suspend fun addUserToDB(user : User) : User? {
        val userDB = DBUser(user.name,user.email,user.phone)
        return suspendCoroutine {
            database.child("users").child(user.firebaseId)
                .setValue(userDB).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        it.resumeWith(Result.success(user))
                    }
                    else{
                        Logger.logDbError("RealTimeDB: Add user to db failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    suspend fun checkUserInDB(firebaseUserId:String) : Boolean{
        return suspendCoroutine {
            database.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(firebaseUserId)){
                        it.resumeWith(Result.success(true))
                    }
                    else{
                        it.resumeWith(Result.success(false))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    it.resumeWith(Result.failure(error.toException()))
                }
            })
        }
    }

    override suspend fun getUserFromDB(userID: Long): User? {
        return null
    }

    suspend fun getUserFromDB(userID: String): User? {
        return suspendCoroutine {
            database.child("users").child(userID)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        task.result.also { snapshot ->
                            Logger.logDbInfo("User from DB $snapshot")
                            val userDb = Utilities.createUserFromHashMap(snapshot?.value as HashMap<*, *>)
                            val user = User(userDb.name,userDb.email,userDb.phone,firebaseId = userID)
                            it.resumeWith(Result.success(user))
                        }
                    }
                    else{
                        Logger.logDbError("RealtimeDB:Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun addNoteToDB(note: Note, user: User?, timeStamp: Date?): Note? {
        val dbNote = DBNote(note.title,note.content,DateTypeConverters().fromDateTime(timeStamp).toString())
        return suspendCoroutine {
            val ref = database.child("users").child(user?.firebaseId!!)
                .child("notes").push()
            ref.setValue(dbNote)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        note.firebaseId = ref.key.toString()
                        it.resumeWith(Result.success(note))
                    }
                    else{
                        Logger.logDbError("RealTimeDB: Write Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun getNotesFromDB(user: User?): List<Note>? {
        val notes = mutableListOf<Note>()
        return suspendCoroutine {
            database.child("users").child(user?.firebaseId!!)
                .child("notes").get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(i in task.result?.children!!){
                            val noteHashMap = i.value as HashMap<String,String>
                            val note = Note(noteHashMap["title"].toString(),noteHashMap["content"].toString(),
                                firebaseId = i.key.toString(),lastModified = DateTypeConverters().toDateTime(noteHashMap["lastModified"]) as Date)
                            notes.add(note)
                        }
                        it.resumeWith(Result.success(notes))
                    }
                    else{
                        Logger.logDbError("RealTimeDB: Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun updateNoteInDB(note: Note, user: User?, timeStamp: Date?): Note? {
        val noteMap = mapOf(
            "title" to note.title,
            "content" to note.content,
            "lastModified" to DateTypeConverters().fromDateTime(timeStamp).toString()
        )

        return suspendCoroutine {
            database.child("users").child(user?.firebaseId!!)
                .child("notes").child(note.firebaseId).updateChildren(noteMap)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        it.resumeWith(Result.success(note))
                    }
                    else{
                        Logger.logDbError("RealTimeDB : Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun deleteNoteFromDB(note: Note, user: User?, timeStamp: Date?): Note? {
        return suspendCoroutine {
            database.child("users").child(user?.firebaseId!!)
                .child("notes").child(note.firebaseId).removeValue()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        it.resumeWith(Result.success(note))
                    }
                    else{
                        Logger.logDbError("RealTimeDB : Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }
}