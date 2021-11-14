package com.example.fundo.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val nid:Long = 0L,
    val fid:String = "",
    val title:String,
    val content:String,
    val lastModified: Date,
)
