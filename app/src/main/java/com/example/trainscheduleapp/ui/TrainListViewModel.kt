// TrainListViewModel.kt
package com.example.trainscheduleapp.ui.trainlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainscheduleapp.data.Train
import com.example.trainscheduleapp.data.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainListViewModel @Inject constructor(
    private val repo: TrainRepository
) : ViewModel() {

    val trains: StateFlow<List<Train>> = repo.getAllTrains()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun addTrain(number: String) {
        repo.addTrain(number)
    }
    // Новый метод удаления
    fun deleteTrain(train: Train) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTrain(train)
        }
    }
}
