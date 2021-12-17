package com.example.fundo.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fundo.data.room.entities.UserEntity

@Dao
interface UserDao {
    @Insert
    fun addUser(userEntity: UserEntity): Long

    @Query("SELECT * from user where uid = :userId")
    fun getUser(userId: Long): UserEntity
}