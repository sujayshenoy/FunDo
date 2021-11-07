package com.example.fundo.services

import android.util.Log
import com.example.fundo.models.DBNote
import com.example.fundo.models.DBUser
import com.example.fundo.wrapper.User
import com.example.fundo.utils.Utilities
import com.example.fundo.wrapper.Note
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DatabaseService {
    private val database = Firebase.database.reference

    fun addUserToDB(user : User, callback : (Boolean) -> Unit){
        val userDB = DBUser(user.name,user.email,user.phone)
        database.child("users").child(AuthService.getCurrentUser()?.uid.toString()).setValue(userDB).addOnCompleteListener {
            if(it.isSuccessful){
                Utilities.addUserToSharedPref(userDB)
                callback(true)
            }
            else{
                Log.e("DB","Write Failed")
                Log.e("DB",it.exception.toString())
                callback(false)
            }
        }
    }

    fun getUserFromDB(callback: (Boolean) -> Unit) {
        database.child("users").child(AuthService.getCurrentUser()?.uid.toString()).get()
            .addOnCompleteListener { status ->
                if(status.isSuccessful) {
                    status.result.also {
                        Log.i("DB","User from DB $it")
                        val user = Utilities.createUserFromHashMap(it?.value as HashMap<*, *>)
                        Utilities.addUserToSharedPref(user)
                        callback(true)
                    }
                }
                else{
                    Log.e("DB","Read Failed")
                    Log.e("DB",status.exception.toString())
                    callback(false)
                }
        }
    }

    fun addNoteToDB(note: Note, callback: (Note?) -> Unit) {
        val dbNote = DBNote(note.title,note.content)
        val ref = database.child("users").child(AuthService.getCurrentUser()?.uid.toString()).child("notes")
            .push()
        ref.setValue(dbNote)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    note.id = ref.key.toString()
                    callback(note)
                }
                else{
                    Log.e("DB","Write Failed")
                    Log.e("DB",it.exception.toString())
                    callback(null)
                }
            }
    }

    fun getNotesFromDB(callback: (List<Note>?) -> Unit) {
        val notes = mutableListOf<Note>()
        database.child("users").child(AuthService.getCurrentUser()?.uid.toString()).child("notes")
            .get().addOnCompleteListener {
                if(it.isSuccessful){
                    for(i in it.result?.children!!){
                        val noteHashMap = i.value as HashMap<String,String>
                        val note = Note(noteHashMap["title"].toString(),noteHashMap["content"].toString(),i.key.toString())
                        notes.add(note)
                    }
                    callback(notes)
                }
                else{
                    Log.e("DB","Read Failed")
                    Log.e("DB",it.exception.toString())
                    callback(null)
                }
            }
    }

    fun updateNoteInDB(note: Note, callback: (Note?) -> Unit){
        val noteMap = mapOf(
            "title" to note.title,
            "content" to note.content
        )

        database.child("users").child(AuthService.getCurrentUser()?.uid.toString())
            .child("notes").child(note.id).updateChildren(noteMap)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    callback(note)
                }
                else{
                    Log.e("DB","Read Failed")
                    Log.e("DB",it.exception.toString())
                    callback(null)
                }
            }
    }
}