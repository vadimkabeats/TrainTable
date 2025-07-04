// TrainEditViewModel.kt
package com.example.trainscheduleapp.ui.trainedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainscheduleapp.data.Station
import com.example.trainscheduleapp.data.Train
import com.example.trainscheduleapp.data.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TrainEditViewModel @Inject constructor(
    private val repo: TrainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trainId: Long = savedStateHandle["trainId"] ?: 0L

    // Номер поезда
    private val _trainNumber = MutableStateFlow("")
    val trainNumber: StateFlow<String> = _trainNumber.asStateFlow()

    // Смещение оповещения (секунд до отправления)
    private val _notifyOffset = MutableStateFlow(10)
    val notifyOffset: StateFlow<Int> = _notifyOffset.asStateFlow()

    // Список станций
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()

    init {
        if (trainId != 0L) {
            // 1) Подгружаем номер и notifyOffset
            viewModelScope.launch {
                repo.getTrain(trainId)
                    .firstOrNull()
                    ?.let { train ->
                        _trainNumber.value = train.number
                        _notifyOffset.value = train.notifyOffsetSec
                    }
            }
            // 2) Подгружаем станции
            viewModelScope.launch {
                repo.getStations(trainId)
                    .collect { list ->
                        _stations.value = list
                    }
            }
        }
    }

    fun onNumberChange(new: String) {
        _trainNumber.value = new
    }

    fun onOffsetChange(newSec: Int) {
        _notifyOffset.value = newSec
    }

    fun addStation() {
        _stations.value = _stations.value + Station(
            trainId = trainId,
            name = "",
            arrivalTime = LocalTime.of(0, 0),
            departureTime = LocalTime.of(0, 0),
            speed = 0
        )
    }

    fun updateStation(index: Int, updated: Station) {
        _stations.value = _stations.value.toMutableList().also { it[index] = updated }
    }

    fun removeStation(index: Int) {
        _stations.value = _stations.value.toMutableList().also { it.removeAt(index) }
    }

    /**
     * Сохраняем поезд и станции «дифф-логикой» с учётом notifyOffsetSec:
     *   1) создаём или обновляем сам поезд, включая поле notifyOffsetSec
     *   2) диффим список станций
     */
    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            // 1) Создаём или обновляем поезд по-старому
            val id = if (trainId == 0L) {
                // а) вставляем только номер
                val newId = repo.addTrain(_trainNumber.value)
                // б) сразу обновляем notifyOffsetSec
                repo.updateTrain(
                    Train(
                        id = newId,
                        number = _trainNumber.value,
                        notifyOffsetSec = _notifyOffset.value
                    )
                )
                newId
            } else {
                // обновляем и number, и offset целиком
                repo.updateTrain(
                    Train(
                        id = trainId,
                        number = _trainNumber.value,
                        notifyOffsetSec = _notifyOffset.value
                    )
                )
                trainId
            }

            // 2) Диффим старые и текущие станции
            val oldStations = repo.getStations(id).first()

            oldStations
                .filter { old -> _stations.value.none { it.id == old.id } }
                .forEach { repo.deleteStation(it) }

            _stations.value.forEach { station ->
                if (station.id == 0L) {
                    repo.addStation(station.copy(trainId = id))
                } else {
                    repo.updateStation(station)
                }
            }

            onDone()
        }
    }
}
