// StationListViewModel.kt
package com.example.trainscheduleapp.ui.stationlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainscheduleapp.data.TrainRepository
import com.example.trainscheduleapp.data.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StationListViewModel @Inject constructor(
    repo: TrainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trainId: Long = savedStateHandle["trainId"] ?: 0L

    /** Список станций текущего поезда */
    val stations: StateFlow<List<Station>> = repo
        .getStations(trainId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
