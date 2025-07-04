package com.example.trainscheduleapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainDao {
    @Query("SELECT * FROM trains")
    fun getAllTrains(): Flow<List<Train>>

    // Синхронный insert, возвращает generatedId
    @Insert
    fun insertTrain(train: Train): Long

    // Синхронный update, возвращает количество обновлённых строк
    @Update
    fun updateTrain(train: Train): Int

    // Синхронный delete, возвращает количество удалённых строк
    @Delete
    fun deleteTrain(train: Train): Int

    @Query("SELECT * FROM trains WHERE id = :trainId")
    fun getTrainById(trainId: Long): Flow<Train>
}