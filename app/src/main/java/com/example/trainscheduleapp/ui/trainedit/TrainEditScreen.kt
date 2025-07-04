package com.example.trainscheduleapp.ui.trainedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trainscheduleapp.data.Station
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainEditScreen(
    trainId: Long,
    onDone: () -> Unit,
    viewModel: TrainEditViewModel = hiltViewModel()
) {
    val number by viewModel.trainNumber.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val offset by viewModel.notifyOffset.collectAsState()
    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (trainId == 0L) "Новый поезд" else "Редактировать поезд") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.save(onDone) }) {
                Text("Сохранить")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scroll)
        ) {
            // Номер поезда
            OutlinedTextField(
                value = number,
                onValueChange = viewModel::onNumberChange,
                label = { Text("Номер поезда") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Выбор смещения уведомления
            OffsetSelector(
                selectedSec = offset,
                onSelect = viewModel::onOffsetChange
            )

            Spacer(Modifier.height(16.dp))

            // Список станций
            Text("Станции:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            stations.forEachIndexed { index, station ->
                key(station.id) {
                    StationEditor(
                        station = station,
                        onChange = { updated -> viewModel.updateStation(index, updated) },
                        onRemove = { viewModel.removeStation(index) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Button(onClick = viewModel::addStation, modifier = Modifier.fillMaxWidth()) {
                Text("Добавить станцию")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OffsetSelector(
    selectedSec: Int,
    onSelect: (Int) -> Unit
) {
    val options = listOf(5, 10, 20, 30)
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = "$selectedSec сек",
            onValueChange = {},
            label = { Text("Оповестить за") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { sec ->
                DropdownMenuItem(
                    text = { Text("$sec сек") },
                    onClick = {
                        onSelect(sec)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StationEditor(
    station: Station,
    onChange: (Station) -> Unit,
    onRemove: () -> Unit
) {
    var name by remember { mutableStateOf(station.name) }
    var arrival by remember { mutableStateOf(station.arrivalTime.toString()) }
    var departure by remember { mutableStateOf(station.departureTime.toString()) }
    var speed by remember { mutableStateOf(station.speed.toString()) }

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(8.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text = "Станция",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onChange(station.copy(name = it))
                },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = arrival,
                    onValueChange = {
                        arrival = it
                        runCatching { LocalTime.parse(it) }.onSuccess { t ->
                            onChange(station.copy(arrivalTime = t))
                        }
                    },
                    label = { Text("Прибытие (HH:mm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = departure,
                    onValueChange = {
                        departure = it
                        runCatching { LocalTime.parse(it) }.onSuccess { t ->
                            onChange(station.copy(departureTime = t))
                        }
                    },
                    label = { Text("Отправление (HH:mm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = speed,
                onValueChange = {
                    speed = it
                    it.toIntOrNull()?.let { v -> onChange(station.copy(speed = v)) }
                },
                label = { Text("Скорость, км/ч") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
