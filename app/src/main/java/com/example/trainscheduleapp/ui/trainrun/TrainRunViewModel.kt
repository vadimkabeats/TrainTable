// TrainRunViewModel.kt
package com.example.trainscheduleapp.ui.trainrun

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainscheduleapp.data.Station
import com.example.trainscheduleapp.data.Train
import com.example.trainscheduleapp.data.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrainRunViewModel @Inject constructor(
    repo: TrainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trainId: Long = savedStateHandle["trainId"] ?: 0L

    /** Поток списка станций для выбранного поезда */
    val stations: StateFlow<List<Station>> = repo
        .getStations(trainId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    val train: StateFlow<Train?> = repo.getTrain(trainId)
        .map { it }  // Flow<Train>
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
