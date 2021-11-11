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

object FirebaseDatabaseService : DatabaseInterface {
    private val database = Firebase.database.reference

    override fun addUserToDB(user : User, callback : (Boolean) -> Unit){
        val userDB = DBUser(user.name,user.email,user.phone)
        database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .setValue(userDB).addOnCompleteListener {
            if(it.isSuccessful){
                Utilities.addUserToSharedPref(userDB)
                callback(true)
            }
            else{
                Logger.logDbError("Write Failed")
                Logger.logDbError(it.exception.toString())
                callback(false)
            }
        }
    }

    override fun getUserFromDB(callback: (Boolean) -> Unit) {
        database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .get().addOnCompleteListener { status ->
                if(status.isSuccessful) {
                    status.result.also {
                        Logger.logDbInfo("User from DB $it")
                        val user = Utilities.createUserFromHashMap(it?.value as HashMap<*, *>)
                        Utilities.addUserToSharedPref(user)
                        callback(true)
                    }
                }
                else{
                    Logger.logDbError("Read Failed")
                    Logger.logDbError(status.exception.toString())
                    callback(false)
                }
        }
    }

    override fun addNoteToDB(note: Note, callback: (Note?) -> Unit) {
        val dbNote = DBNote(note.title,note.content)
        val ref = database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .child("notes").push()
        ref.setValue(dbNote)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    note.id = ref.key.toString()
                    callback(note)
                }
                else{
                    Logger.logDbError("Write Failed")
                    Logger.logDbError(it.exception.toString())
                    callback(null)
                }
            }
    }

    override fun getNotesFromDB(callback: (List<Note>?) -> Unit) {
        val notes = mutableListOf<Note>()
        database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .child("notes").get().addOnCompleteListener {
                if(it.isSuccessful){
                    for(i in it.result?.children!!){
                        val noteHashMap = i.value as HashMap<String,String>
                        val note = Note(noteHashMap["title"].toString(),noteHashMap["content"].toString(),
                            i.key.toString())
                        notes.add(note)
                    }
                    callback(notes)
                }
                else{
                    Logger.logDbError("Read Failed")
                    Logger.logDbError(it.exception.toString())
                    callback(null)
                }
            }
    }

    override fun updateNoteInDB(note: Note, callback: (Note?) -> Unit){
        val noteMap = mapOf(
            "title" to note.title,
            "content" to note.content
        )

        database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .child("notes").child(note.id).updateChildren(noteMap)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    callback(note)
                }
                else{
                    Logger.logDbError("Read Failed")
                    Logger.logDbError(it.exception.toString())
                    callback(null)
                }
            }
    }

    override fun deleteNoteFromDB(note: Note, callback: (Note?) -> Unit){
        database.child("users").child(FirebaseAuthService.getCurrentUser()?.uid.toString())
            .child("notes").child(note.id).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    callback(note)
                }
                else{
                    callback(null)
                }
            }
    }
}