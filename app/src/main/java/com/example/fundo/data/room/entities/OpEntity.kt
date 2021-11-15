package com.example.fundo.data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "operation")
data class OpEntity(
    @PrimaryKey
    val fid: String,
    val op: Int,
)
