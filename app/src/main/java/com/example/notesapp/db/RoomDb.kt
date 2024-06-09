package com.example.notesapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.db.dao.NoteDao
import com.example.notesapp.db.entites.NoteEntity


@Database(entities = [NoteEntity::class], version = 1)
abstract class RoomDb:RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        private var INSTANCE: RoomDb? = null
        fun getInstance(context: Context): RoomDb? {
            if (INSTANCE == null) {
                synchronized(RoomDb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDb::class.java, "Notes"
                    )
                        .build()
                }
            }
            return INSTANCE
        }

    }
}
