package com.example.trainscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trainscheduleapp.ui.trainlist.TrainListScreen
import com.example.trainscheduleapp.ui.trainedit.TrainEditScreen
import com.example.trainscheduleapp.ui.trainrun.TrainRunScreen
import dagger.hilt.android.AndroidEntryPoint
import com.example.trainscheduleapp.ui.stationlist.StationListScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Убираем все манипуляции с insets — пусть система сама создаёт отступы
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
                TrainScheduleApp()

        }
    }
}

@Composable
fun TrainScheduleApp() {

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "train_list"
        ) {
            // Список поездов
            composable("train_list") {
                TrainListScreen(
                    onAdd = { navController.navigate("train_edit/0") },
                    onEdit = { id -> navController.navigate("train_edit/$id") },
                    onShowStations = { id, number ->
                        navController.navigate("station_list/$id/$number")
                    },
                    onRun = { id ->
                        navController.navigate("train_run/$id")
                    }
                )
            }

            // Экран создания/редактирования поезда
            composable(
                route = "train_edit/{trainId}",
                arguments = listOf(navArgument("trainId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val trainId = backStackEntry.arguments?.getLong("trainId") ?: 0L
                TrainEditScreen(
                    trainId = trainId,
                    onDone = { navController.popBackStack() }  // <-- обязательно указать onDone
                )
            }
            // новый маршрут
            composable(
                route = "station_list/{trainId}/{trainNumber}",
                arguments = listOf(
                    navArgument("trainId") { type = NavType.LongType },
                    navArgument("trainNumber") { type = NavType.StringType }
                )
            ) { back ->
                val number = back.arguments!!.getString("trainNumber") ?: ""
                StationListScreen(
                    trainNumber = number,
                    onBack = { navController.popBackStack() }
                )
            }

            // Экран «в пути»
            composable(
                route = "train_run/{trainId}",
                arguments = listOf(navArgument("trainId") {
                    type = NavType.LongType
                })
            ) { backStackEntry ->
                val trainId = backStackEntry.arguments?.getLong("trainId") ?: 0L
                TrainRunScreen(
                    trainId = trainId,
                    onDone = { navController.popBackStack() }  // <-- и здесь тоже
                )
            }
        }
    }

