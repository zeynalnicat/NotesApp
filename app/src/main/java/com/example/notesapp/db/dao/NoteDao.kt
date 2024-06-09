package com.example.notesapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.notesapp.db.entites.NoteEntity


@Dao
interface NoteDao {


    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Query("SELECT * FROM notes ")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("Select * from notes where id=:id")
    suspend fun getNote(id: Int): NoteEntity

}