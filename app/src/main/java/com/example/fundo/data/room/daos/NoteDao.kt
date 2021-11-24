package com.example.fundo.data.room.daos

import androidx.room.*
import com.example.fundo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Query("SELECT * from note WHERE archived = 0")
    suspend fun getNotes(): List<NoteEntity>

    @Query("SELECT * from note WHERE archived = 0 LIMIT :limit OFFSET :offset")
    suspend fun getNotesPaged(limit: Int, offset: Int): List<NoteEntity>

    @Query("SELECT COUNT(*) from note WHERE archived = 0")
    suspend fun getNotesCount(): Int

    @Query("SELECT * from note WHERE archived = 1")
    suspend fun getArchives(): List<NoteEntity>

    @Query("SELECT * from note WHERE archived = 1 LIMIT :limit OFFSET :offset")
    suspend fun getArchivesPaged(limit: Int, offset: Int): List<NoteEntity>

    @Query("SELECT * from note WHERE NULLIF(reminder,'') IS NOT NULL")
    suspend fun getReminders(): List<NoteEntity>

    @Query("SELECT * from note WHERE NULLIF(reminder,'') IS NOT NULL LIMIT :limit OFFSET :offset")
    suspend fun getRemindersPaged(limit: Int, offset: Int): List<NoteEntity>

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("DELETE from note")
    suspend fun nukeTable()
}