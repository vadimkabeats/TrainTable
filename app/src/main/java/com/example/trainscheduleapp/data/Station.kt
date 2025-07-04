package com.example.trainscheduleapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "stations",
    foreignKeys = [
        ForeignKey(
            entity = Train::class,
            parentColumns = ["id"],
            childColumns = ["trainId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("trainId")]
)
data class Station(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val trainId: Long,        // ссылка на поезд

    val name: String,         // название станции

    val arrivalTime: LocalTime,   // время прибытия

    val departureTime: LocalTime, // время отправления

    val speed: Int            // скорость до этой станции (км/ч)
)
