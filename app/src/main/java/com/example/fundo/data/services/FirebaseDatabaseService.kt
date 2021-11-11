package com.example.fundo.data.services

import com.example.fundo.data.models.DBNote
import com.example.fundo.data.models.DBUser
import com.example.fundo.data.wrappers.User
import com.example.fundo.common.Utilities
import com.example.fundo.data.wrappers.Note
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.Logger
import com.example.fundo.interfaces.DatabaseInterface
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

object FirebaseDatabaseService : DatabaseInterface {
    private val database = Firebase.database.reference
    override suspend fun addUserToDB(user : User) : Boolean{
        val userDB = DBUser(user.name,user.email,user.phone)
        return suspendCoroutine {
            database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .setValue(userDB).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Utilities.addUserToSharedPref(userDB)
                        Logger.logDbInfo("User added to DB")
                        it.resumeWith(Result.success(true))
                    }
                    else{
                        Logger.logDbError("RealTimeDB: Add user to db failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }

    }

    override suspend fun getUserFromDB() : Boolean {
        return suspendCoroutine {
            database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        task.result.also { snapshot ->
                            Logger.logDbInfo("User from DB $snapshot")
                            val user = Utilities.createUserFromHashMap(snapshot?.value as HashMap<*, *>)
                            Utilities.addUserToSharedPref(user)
                            it.resumeWith(Result.success(true))
                        }
                    }
                    else{
                        Logger.logDbError("RealtimeDB:Read Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun addNoteToDB(note: Note) : Note? {
        val dbNote = DBNote(note.title,note.content)
        return suspendCoroutine {
            val ref = database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .child("notes").push()
            ref.setValue(dbNote)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        note.id = ref.key.toString()
                        it.resumeWith(Result.success(note))
                    }
                    else{
                        Logger.logDbError("RealTimeDB: Write Failed")
                        it.resumeWith(Result.failure(task.exception!!))
                    }
                }
        }
    }

    override suspend fun getNotesFromDB() : List<Note> {
        val notes = mutableListOf<Note>()
        return suspendCoroutine {
            database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .child("notes").get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(i in task.result?.children!!){
                            val noteHashMap = i.value as HashMap<String,String>
                            val note = Note(noteHashMap["title"].toString(),noteHashMap["content"].toString(),
                                i.key.toString())
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

    override suspend fun updateNoteInDB(note: Note) : Note{
        val noteMap = mapOf(
            "title" to note.title,
            "content" to note.content
        )

        return suspendCoroutine {
            database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .child("notes").child(note.id).updateChildren(noteMap)
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

    override suspend fun deleteNoteFromDB(note: Note):Note{
        return suspendCoroutine {
            database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
                .child("notes").child(note.id).removeValue()
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