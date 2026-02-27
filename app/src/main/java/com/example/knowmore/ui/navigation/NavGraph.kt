package com.example.knowmore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.knowmore.data.local.AppDatabase
import com.example.knowmore.data.repository.WordRepository
import com.example.knowmore.ui.screens.*
import com.example.knowmore.ui.viewmodel.*

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = WordRepository(database.wordDao())

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(repository)
            )
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToFlashcards = { navController.navigate(Screen.Flashcards.route) },
                onNavigateToWordList = { navController.navigate(Screen.WordList.route) },
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToAddWord = { navController.navigate(Screen.AddWord.route) }
            )
        }

        composable(Screen.Flashcards.route) {
            val viewModel: FlashcardViewModel = viewModel(
                factory = FlashcardViewModel.Factory(repository)
            )
            FlashcardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WordList.route) {
            val viewModel: WordListViewModel = viewModel(
                factory = WordListViewModel.Factory(repository)
            )
            WordListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddWord = { navController.navigate(Screen.AddWord.route) }
            )
        }

        composable(Screen.Progress.route) {
            val viewModel: ProgressViewModel = viewModel(
                factory = ProgressViewModel.Factory(repository)
            )
            ProgressScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddWord.route) {
            val viewModel: WordListViewModel = viewModel(
                factory = WordListViewModel.Factory(repository)
            )
            AddWordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
