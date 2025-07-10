// TrainRunScreen.kt
package com.example.trainscheduleapp.ui.trainrun

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trainscheduleapp.R
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainRunScreen(
    trainId: Long,
    onDone: () -> Unit,
    viewModel: TrainRunViewModel = hiltViewModel()
) {
    val stations by viewModel.stations.collectAsState()
    val train    by viewModel.train.collectAsState()

    val context  = LocalContext.current
    val activity = LocalActivity.current
    val view     = LocalView.current

    // 1) Держим экран включённым
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
    // 2) Фиксируем ландшафт
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    // 3) Состояние времени и флагов
    var currentTime   by remember { mutableStateOf(LocalTime.now()) }
    var playedAtIndex by remember { mutableStateOf(-1) }
    val notifyOffset = train?.notifyOffsetSec ?: 10
    // Сбрасываем флаги при повторном входе на экран
    LaunchedEffect(trainId) {
        playedAtIndex = -1
    }

    // MediaPlayer для вашего звука
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.alert).apply {
            setOnCompletionListener { it.seekTo(0) }
        }
    }

    // 4) Тикер — обновляем currentTime и играем звук за 4 сек до отправления
    LaunchedEffect(stations) {
        while (true) {
            currentTime = LocalTime.now()

            // пересчитываем индекс текущей станции
            val idx = stations.indexOfFirst { it.departureTime >= currentTime }
                .let { if (it == -1) stations.size - 1 else it }

            stations.getOrNull(idx)?.let { s ->
                val secUntil = Duration.between(currentTime, s.departureTime).seconds.toInt()
                if (secUntil == notifyOffset && idx != playedAtIndex) {
                    mediaPlayer.start()
                    playedAtIndex = idx
                }
            }

            delay(1000L)
        }
    }

    // Локально высчитываем currentIndex для UI (без побочных эффектов)
    val currentIndex = remember(currentTime, stations) {
        stations.indexOfFirst { it.departureTime >= currentTime }
            .let { if (it == -1) stations.size - 1 else it }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Color.Black,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        text = train?.let { "Поезд №${it.number}" } ?: "Поезд #$trainId",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (stations.isEmpty()) {
                Text("Нет станций для этого поезда",fontSize = 18.sp,  color = Color.White)
            } else {
                val s = stations[currentIndex]
                val secUntil = Duration.between(currentTime, s.departureTime).seconds.toInt()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        // паддинг сверху — под шапку, делаем поменьше
                        .padding(top = 1.dp)
                ) {
                    // Текущее время
                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        fontSize = 50.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(1.dp))

                    // Особый текст для первой/остальных станций
                    if (currentIndex == 0) {
                        Text(
                            text = "предупр.получ,алсн и р/с вкл,стоян тор.отпущ,давл в тор и пит маг в нор,ск по стр 40,сигн ост не под,от посл опр 7 мин, вр.согл.расп.законч,вых,лок.зел",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "р/c,алкн вкл вр стоя 1 м в торм маг 5 скор.след вых.лок.зел",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(1.dp))

                    // 3) Данные станции или отсчёт
                    if (secUntil in 1..60) {
                        Text(
                            text = "Минута готовности перед отправлением",
                            fontSize = 20.sp,             // чуть больше, чем метки
                            color = Color.White
                        )
                        Spacer(Modifier.height(1.dp))
                        Text(
                            text = "Осталось: $secUntil сек.",
                            fontSize = 48.sp,             // 1.5× от 36 → 54
                            color = Color.White
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Станция:",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(1.dp))  // маленький отступ между ними
                            Text(
                                text = s.name,
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(1.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Прибытие:", fontSize = 18.sp, color = Color.White)
                            Spacer(Modifier.width(1.dp))
                            Text(s.arrivalTime.toString(), fontSize = 44.sp, color = Color.White)
                        }
                        Spacer(Modifier.height(1.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Отправление:", fontSize = 18.sp, color = Color.White)
                            Spacer(Modifier.width(1.dp))
                            Text(s.departureTime.toString(), fontSize = 44.sp, color = Color.White)
                        }
                        Spacer(Modifier.height(1.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Скорость:", fontSize = 18.sp, color = Color.White)
                            Spacer(Modifier.width(1.dp))
                            Text(s.speed.toString(), fontSize = 44.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}