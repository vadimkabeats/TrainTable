package com.example.trainscheduleapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
    @Query("""
        SELECT * FROM stations
        WHERE trainId = :trainId
        ORDER BY arrivalTime
    """)
    fun getStationsForTrain(trainId: Long): Flow<List<Station>>

    @Insert
    fun insertStation(station: Station): Long

    @Update
    fun updateStation(station: Station): Int

    @Delete
    fun deleteStation(station: Station): Int
}
