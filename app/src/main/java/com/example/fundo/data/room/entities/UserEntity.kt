package com.example.fundo.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val uid:Long = 0L,
    val fid : String = "",
    val name:String,
    val email:String,
    val phone:String,
)