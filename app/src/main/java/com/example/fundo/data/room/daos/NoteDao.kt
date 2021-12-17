package com.example.fundo.data.room.daos

import androidx.room.*
import com.example.fundo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    fun addNote(noteEntity: NoteEntity): Long

    @Query("SELECT * from note WHERE archived = 0")
    fun getNotes(): List<NoteEntity>

    @Query("SELECT * from note WHERE archived = 0 LIMIT :limit OFFSET :offset")
    fun getNotesPaged(limit: Int, offset: Int): List<NoteEntity>

    @Query("SELECT COUNT(*) from note WHERE archived = 0")
    fun getNotesCount(): Int

    @Query("SELECT * from note WHERE archived = 1")
    fun getArchives(): List<NoteEntity>

    @Query("SELECT * from note WHERE archived = 1 LIMIT :limit OFFSET :offset")
    fun getArchivesPaged(limit: Int, offset: Int): List<NoteEntity>

    @Query("SELECT * from note WHERE NULLIF(reminder,'') IS NOT NULL")
    fun getReminders(): List<NoteEntity>

    @Query("SELECT * from note WHERE NULLIF(reminder,'') IS NOT NULL LIMIT :limit OFFSET :offset")
    fun getRemindersPaged(limit: Int, offset: Int): List<NoteEntity>

    @Update
    fun updateNote(noteEntity: NoteEntity)

    @Delete
    fun deleteNote(noteEntity: NoteEntity)

    @Query("DELETE from note")
    fun nukeTable()
}