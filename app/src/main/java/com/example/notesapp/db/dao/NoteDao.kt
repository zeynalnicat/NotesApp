package com.example.notesapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.db.entites.NoteEntity


@Dao
interface NoteDao {


    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Query("SELECT * FROM notes ")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("Select * from notes where id=:noteId")
    suspend fun getNote(noteId: Int): NoteEntity

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)
}