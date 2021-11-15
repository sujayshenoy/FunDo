package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.config.Constants.DELETE_OP_CODE
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object SyncDB {
    suspend fun syncNow(context: Context,user: User){
        val latestNotes = getLatestNotesFromDB(context,user)
        DatabaseService.clearNoteAndOp()
        latestNotes.forEach{
            DatabaseService.addNoteToLocalDB(context,it,user)
        }
    }

    private suspend fun getLatestNotesFromDB(context: Context, user: User) : List<Note>{
       return withContext(Dispatchers.IO){
            val sqlNotesList = DatabaseService.getNotesFromDB(user)
            val localNotesList = mutableListOf<Note>()
            if(sqlNotesList != null){
                localNotesList.addAll(sqlNotesList)
            }
            val cloudNotesList = DatabaseService.getNotesFromCloud(user)
            val latestNotes = mutableListOf<Note>()

            if(cloudNotesList != null){
                for(cloudNote in cloudNotesList){
                    var localNoteIndexCounter = 0
                    for(localNote in localNotesList){
                        if(cloudNote.firebaseId == localNote.firebaseId){
                            val res = compareTimeStamp(localNote,cloudNote)
                            if(res){
                                latestNotes.add(localNote)
                                FirebaseDatabaseService.updateNoteInDB(
                                    context,
                                    localNote,
                                    user,
                                    localNote.lastModified
                                )
                            }
                            else{
                                latestNotes.add(cloudNote)
                            }
                            break
                        }
                        localNoteIndexCounter++
                    }
                    if(localNoteIndexCounter == localNotesList.size){
                        localNotesList.add(cloudNote)
                        latestNotes.add(cloudNote)
                    }
                }

                for(localNote in localNotesList){
                    var cloudNoteIndexCounter = 0
                    for(cloudNote in cloudNotesList){
                        if(localNote.firebaseId == cloudNote.firebaseId){
                            if(getOpCode(localNote) == DELETE_OP_CODE){
                                FirebaseDatabaseService.deleteNoteFromDB(
                                    context,
                                    localNote,
                                    user,
                                    Date(System.currentTimeMillis())
                                )
                                latestNotes.remove(localNote)
                            }
                            break
                        }
                        cloudNoteIndexCounter++
                    }
                    if(cloudNoteIndexCounter == cloudNotesList.size){
                        val opCode = getOpCode(localNote)
                        if(opCode != -1){
                            latestNotes.add(localNote)
                            FirebaseDatabaseService.addNoteToDB(
                                context,
                                localNote,
                                user,
                                localNote.lastModified
                            )
                        }
                    }
                }
                return@withContext latestNotes
            }
           else{
               return@withContext listOf<Note>()
            }
        }
    }

    private suspend fun getOpCode(localNote: Note): Int {
        return withContext(Dispatchers.IO){
            return@withContext DatabaseService.getOpCode(localNote)
        }
    }

    private fun compareTimeStamp(localNote: Note, cloudNote: Note): Boolean {
        val localDate = localNote.lastModified
        val cloudDate = cloudNote.lastModified
        return if(localDate != null && cloudDate != null){
            localDate.after(cloudDate)
        }else{
            false
        }
    }
}