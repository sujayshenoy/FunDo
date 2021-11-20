package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.R
import com.example.fundo.common.NetworkService
import com.example.fundo.common.Utilities
import com.example.fundo.config.Constants.DELETE_OP_CODE
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SyncDB(private val context: Context) {
    suspend fun syncNow(user: User) {
        if (NetworkService.isNetworkConnected(context)) {
            val latestNotes = getLatestNotesFromDB(user)
            DatabaseService.getInstance(context).clearNoteAndOp()
            latestNotes.forEach {
                DatabaseService.getInstance(context).addNoteToLocalDB(it, user)
            }
        } else {
            Utilities.displayToast(context, context.getString(R.string.no_internet_error))
        }
    }

    private suspend fun getLatestNotesFromDB(user: User): List<Note> {
        return withContext(Dispatchers.IO) {
            val localNotesList =
                DatabaseService.getInstance(context).getNotesFromDB(user)?.toMutableList()
                    ?: mutableListOf()
            val cloudNotesList = DatabaseService.getInstance(context).getNotesFromCloud(user)
            val latestNotes = mutableListOf<Note>()

            if (cloudNotesList != null) {
                for (cloudNote in cloudNotesList) {
                    var localNoteIndexCounter = 0
                    for (localNote in localNotesList) {
                        if (cloudNote.firebaseId == localNote.firebaseId) {
                            val res = compareTimeStamp(localNote, cloudNote)
                            if (res) {
                                latestNotes.add(localNote)
                                FirebaseDatabaseService.getInstance().updateNoteInDB(
                                    localNote,
                                    user,
                                    localNote.lastModified
                                )
                            } else {
                                latestNotes.add(cloudNote)
                            }
                            break
                        }
                        localNoteIndexCounter++
                    }
                    if (localNoteIndexCounter == localNotesList.size) {
                        localNotesList.add(cloudNote)
                        latestNotes.add(cloudNote)
                    }
                }

                for (localNote in localNotesList) {
                    var cloudNoteIndexCounter = 0
                    for (cloudNote in cloudNotesList) {
                        if (localNote.firebaseId == cloudNote.firebaseId) {
                            if (getOpCode(localNote) == DELETE_OP_CODE) {
                                FirebaseDatabaseService.getInstance().deleteNoteFromDB(
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
                    if (cloudNoteIndexCounter == cloudNotesList.size) {
                        val opCode = getOpCode(localNote)
                        if (opCode != -1) {
                            latestNotes.add(localNote)
                            FirebaseDatabaseService.getInstance().addNoteToDB(
                                localNote,
                                user,
                                localNote.lastModified
                            )
                        }
                    }
                }
                return@withContext latestNotes
            } else {
                return@withContext listOf<Note>()
            }
        }
    }

    private suspend fun getOpCode(localNote: Note): Int {
        return withContext(Dispatchers.IO) {
            return@withContext DatabaseService.getInstance(context).getOpCode(localNote)
        }
    }

    private fun compareTimeStamp(localNote: Note, cloudNote: Note): Boolean {
        val localDate = localNote.lastModified
        val cloudDate = cloudNote.lastModified
        return if (localDate != null && cloudDate != null) {
            localDate.after(cloudDate)
        } else {
            false
        }
    }
}