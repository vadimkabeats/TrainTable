package com.example.trainscheduleapp.ui.trainlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trainscheduleapp.data.Train
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onShowStations: (Long, String) -> Unit,  // <-- уже есть
    onRun: (Long) -> Unit,                  // <-- добавляем параметр onRun
    viewModel: TrainListViewModel = hiltViewModel()
) {
    val trains by viewModel.trains.collectAsState()
    val scope = rememberCoroutineScope()
    var toDelete by remember { mutableStateOf<Train?>(null) }

    toDelete?.let { train ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Удалить поезд") },
            text = { Text("Вы действительно хотите удалить поезд №${train.number}?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { viewModel.deleteTrain(train) }
                    toDelete = null
                },
                    ) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Нет") }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Поезда") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        if (trains.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Список поездов пуст")
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(trains) { train ->
                    TrainListItem(
                        train = train,
                        onEdit = { onEdit(train.id) },
                        onShowStations = { onShowStations(train.id, train.number) }, // <-- сюда
                        onRun = { onRun(train.id) },                                // <-- и сюда
                        onDelete = { toDelete = train }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun TrainListItem(
    train: Train,
    onEdit: () -> Unit,
    onShowStations: () -> Unit,  // <-- новый параметр
    onRun: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowStations)  // клик по строке — список станций
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Поезд №${train.number}", Modifier.weight(1f))
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
        }
        IconButton(onClick = onRun) {
            Icon(Icons.Default.PlayArrow, contentDescription = "В путь")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить")
        }
    }
}
