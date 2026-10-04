package ru.university.taskcalendar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,       // "2026-05-01"
    val time: String,       // "09:00"
    val isDone: Boolean = false
)