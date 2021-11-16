package com.example.fundo.data.room.daos

import androidx.room.*
import com.example.fundo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Query("SELECT * from note")
    suspend fun getNotes(): List<NoteEntity>

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("DELETE from note")
    suspend fun nukeTable()
}