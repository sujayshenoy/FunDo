package com.example.fundo.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fundo.config.Constants.DATABASE_NAME
import com.example.fundo.data.room.daos.NoteDao
import com.example.fundo.data.room.daos.UserDao
import com.example.fundo.data.room.entities.NoteEntity
import com.example.fundo.data.room.entities.UserEntity

@Database(entities = [UserEntity::class,NoteEntity::class],version = 1,exportSchema = false)
@TypeConverters(DateTypeConverters::class)
abstract class FunDoDatabase:RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun noteDao() : NoteDao

    companion object {
        @Volatile
        private var INSTANCE: FunDoDatabase?= null

        fun getDatabase(context: Context): FunDoDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FunDoDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun clearAll(){
        this.clearAllTables()
    }
}