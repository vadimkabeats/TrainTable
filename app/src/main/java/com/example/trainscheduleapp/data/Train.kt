package com.example.trainscheduleapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trains")
data class Train(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val notifyOffsetSec: Int = 10  // номер поезда
)
