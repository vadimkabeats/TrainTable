// TrainRepository.kt
package com.example.trainscheduleapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TrainRepository(private val db: AppDatabase) {
    private val trainDao = db.trainDao()
    private val stationDao = db.stationDao()

    fun getAllTrains(): Flow<List<Train>> = trainDao.getAllTrains()

    suspend fun addTrain(number: String): Long = withContext(Dispatchers.IO) {
        trainDao.insertTrain(Train(number = number))
    }

    suspend fun updateTrain(train: Train): Int = withContext(Dispatchers.IO) {
        trainDao.updateTrain(train)
    }

    suspend fun deleteTrain(train: Train): Int = withContext(Dispatchers.IO) {
        trainDao.deleteTrain(train)
    }

    fun getStations(trainId: Long): Flow<List<Station>> =
        stationDao.getStationsForTrain(trainId)

    suspend fun addStation(station: Station): Long = withContext(Dispatchers.IO) {
        stationDao.insertStation(station)
    }

    suspend fun updateStation(station: Station): Int = withContext(Dispatchers.IO) {
        stationDao.updateStation(station)
    }

    suspend fun deleteStation(station: Station): Int = withContext(Dispatchers.IO) {
        stationDao.deleteStation(station)
    }
    fun getTrain(trainId: Long): Flow<Train> =
        db.trainDao().getTrainById(trainId)
}