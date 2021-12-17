package com.example.fundo.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fundo.data.room.entities.OpEntity

@Dao
interface OpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOp(opEntity: OpEntity): Long

    @Query("SELECT * from operation WHERE fid = :noteId")
    fun getOpCode(noteId: String): OpEntity

    @Query("DELETE from operation")
    fun nukeOp()
}