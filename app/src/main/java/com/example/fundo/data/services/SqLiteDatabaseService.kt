package com.example.fundo.data.services

import android.content.Context
import com.example.fundo.common.Logger
import com.example.fundo.config.Constants.CREATE_OP_CODE
import com.example.fundo.config.Constants.DELETE_OP_CODE
import com.example.fundo.config.Constants.UPDATE_OP_CODE
import com.example.fundo.data.room.FunDoDatabase
import com.example.fundo.data.room.entities.NoteEntity
import com.example.fundo.data.room.entities.OpEntity
import com.example.fundo.data.room.entities.UserEntity
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.interfaces.DatabaseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class SqLiteDatabaseService(context: Context) : DatabaseInterface {
    private val funDoDatabase = FunDoDatabase.getDatabase(context)
    private val userDao = funDoDatabase.userDao()
    private val noteDao = funDoDatabase.noteDao()
    private val opDao = funDoDatabase.opDao()

    override suspend fun addUserToDB(user: User): User? {
        return withContext(Dispatchers.IO) {
            val userEntity = UserEntity(
                name = user.name,
                email = user.email,
                phone = user.phone,
                fid = user.firebaseId
            )
            val id = userDao.addUser(userEntity)
            user.id = id
            if (user.id > 0) user else throw Exception("Add User to Database Failed")
        }
    }

    override suspend fun getUserFromDB(userID: Long): User? {
        return withContext(Dispatchers.IO) {
            val userEntity = userDao.getUser(userID)
            val user = User(
                userEntity.name, userEntity.email, userEntity.phone,
                id = userEntity.uid, firebaseId = userEntity.fid
            )
            user
        }
    }

    override suspend fun addNoteToDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return withContext(Dispatchers.IO) {
            val noteEntity = NoteEntity(
                fid = note.firebaseId,
                title = note.title,
                content = note.content,
                lastModified = timeStamp!!,
                archived = note.archived
            )
            note.id = noteDao.addNote(noteEntity)
            if (!onlineMode) {
                val opEntity = OpEntity(note.firebaseId, CREATE_OP_CODE)
                opDao.addOp(opEntity)
            }
            note
        }
    }

    override suspend fun getNotesFromDB(user: User?): List<Note>? {
        return withContext(Dispatchers.IO) {
            val noteEntityList = noteDao.getNotes()
            val noteList = mutableListOf<Note>()
            for (i in noteEntityList) {
                val note = Note(
                    title = i.title, content = i.content, id = i.nid,
                    firebaseId = i.fid, lastModified = i.lastModified,
                    archived = i.archived
                )
                noteList.add(note)
            }
            noteList
        }
    }

    suspend fun getArchivedNotes(user: User?): List<Note> {
        return withContext(Dispatchers.IO) {
            val noteEntityList = noteDao.getArchives()
            val noteList = mutableListOf<Note>()
            for (i in noteEntityList) {
                val note = Note(
                    title = i.title, content = i.content, id = i.nid,
                    firebaseId = i.fid, lastModified = i.lastModified,
                    archived = i.archived
                )
                noteList.add(note)
            }
            noteList
        }
    }


    override suspend fun updateNoteInDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return withContext(Dispatchers.IO) {
            val noteEntity = NoteEntity(
                note.id,
                note.firebaseId,
                note.title,
                note.content,
                lastModified = timeStamp!!,
                archived = note.archived
            )
            Logger.logInfo("NoteEntity: $noteEntity")
            noteDao.updateNote(noteEntity)
            if (!onlineMode) {
                val opEntity = OpEntity(note.firebaseId, UPDATE_OP_CODE)
                opDao.addOp(opEntity)
            }
            note
        }
    }

    override suspend fun deleteNoteFromDB(
        note: Note,
        user: User?,
        timeStamp: Date?,
        onlineMode: Boolean
    ): Note? {
        return withContext(Dispatchers.IO) {
            val noteEntity = NoteEntity(
                note.id,
                note.firebaseId,
                note.title,
                note.content,
                lastModified = timeStamp!!
            )
            noteDao.deleteNote(noteEntity)
            if (!onlineMode) {
                if (note.firebaseId.isNotEmpty()) {
                    val opEntity = OpEntity(note.firebaseId, DELETE_OP_CODE)
                    opDao.addOp(opEntity)
                }
            }
            note
        }
    }

    suspend fun getOpCode(note: Note): Int {
        return withContext(Dispatchers.IO) {
            val opc = opDao.getOpCode(note.firebaseId)
            if (opc != null) {
                return@withContext opc.op
            } else {
                return@withContext -1
            }
        }
    }

    suspend fun clearNoteAndOp() {
        noteDao.nukeTable()
        opDao.nukeOp()
    }

    fun clearAll() {
        funDoDatabase.clearAll()
    }
}