// StationListScreen.kt
package com.example.trainscheduleapp.ui.stationlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trainscheduleapp.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationListScreen(
    trainNumber: String,
    onBack: () -> Unit,
    viewModel: StationListViewModel = hiltViewModel()
) {
    val stations by viewModel.stations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поезд №$trainNumber: станции") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (stations.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("У этого поезда ещё нет станций")
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(vertical = 8.dp)
            ) {
                items(stations) { s ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(text = s.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("Прибытие: ${s.arrivalTime}")
                            Text("Отправление: ${s.departureTime}")
                            Text("Скорость: ${s.speed} км/ч")
                        }
                    }
                }
            }
        }
    }
}
