package com.example.notesapp.db.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity("Notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int ,
    val title :String ,
    val content : String,
):Serializable
